package com.ballon.backend.services;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ballon.backend.models.Reserva;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio responsable del envío de correos transaccionales.
 *
 * Diseño:
 * - Los métodos son @Async para que la petición HTTP del usuario no espere
 *   a que el SMTP responda. Si tarda, tarda en background.
 * - Cualquier excepción se captura y se loguea, NUNCA se propaga. El email
 *   es una notificación secundaria: si falla, la reserva (que ya está
 *   guardada en BD) NO debe romperse.
 * - El correo de confirmación incluye el QR de check-in como imagen inline.
 *   Se referencia desde el HTML con `<img src="cid:qr-code">` y los bytes
 *   se adjuntan con MimeMessageHelper.addInline. Es la técnica estándar
 *   que usan Booking, Amazon, etc. para sus QRs por correo.
 * - HTML inline en lugar de plantillas Thymeleaf para no añadir dependencias.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /** Content-ID con el que el HTML referencia el QR embebido (src="cid:..."). */
    private static final String QR_CID = "qr-code";

    private final JavaMailSender mailSender;
    private final QrCodeService qrCodeService;

    @Value("${ballon.mail.from}")
    private String from;

    @Value("${ballon.mail.from-name}")
    private String fromName;

    private static final Locale ES = new Locale("es", "ES");
    private static final DateTimeFormatter FECHA_FMT =
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", ES);

    // ─── Envíos públicos ────────────────────────────────────────────────────

    @Async
    public void enviarConfirmacionReserva(Reserva reserva) {
        try {
            String asunto = "Reserva confirmada - "
                + reserva.getPista().getPolideportivo().getNombre();

            // Generamos el QR a partir del token de la reserva. Si fallara la
            // generación, mandamos el correo igual pero sin imagen (el usuario
            // tiene el token como texto como fallback en el cuerpo del mail).
            byte[] qrBytes = null;
            try {
                qrBytes = qrCodeService.generarPng(reserva.getTokenQr());
            } catch (Exception e) {
                log.warn("No se pudo generar el QR de la reserva {}: {}",
                    reserva.getIdReserva(), e.getMessage());
            }

            String html = construirHtmlConfirmacion(reserva, qrBytes != null);
            enviarHtml(reserva.getUsuario().getEmail(), asunto, html, qrBytes);
        } catch (Exception e) {
            log.error("No se pudo enviar email de confirmación (reserva {}): {}",
                reserva.getIdReserva(), e.getMessage());
        }
    }

    @Async
    public void enviarCancelacionReserva(Reserva reserva) {
        try {
            String asunto = "Reserva cancelada - "
                + reserva.getPista().getPolideportivo().getNombre();
            String html = construirHtmlCancelacion(reserva);
            // El correo de cancelación no lleva QR: ya no sirve para nada.
            enviarHtml(reserva.getUsuario().getEmail(), asunto, html, null);
        } catch (Exception e) {
            log.error("No se pudo enviar email de cancelación (reserva {}): {}",
                reserva.getIdReserva(), e.getMessage());
        }
    }

    // ─── Envío real ─────────────────────────────────────────────────────────

    /**
     * Envío genérico de HTML, con QR inline opcional.
     *
     * @param qrBytes bytes PNG del QR a embeber. Si es null o vacío, el correo
     *                se envía sin imagen inline.
     */
    private void enviarHtml(String destinatario, String asunto, String html, byte[] qrBytes)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        // true en el segundo parámetro = multipart (necesario para inline images)
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(from, fromName);
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(html, true); // true = es HTML

        // OJO: addInline debe ir DESPUÉS de setText. Spring lo exige así.
        if (qrBytes != null && qrBytes.length > 0) {
            helper.addInline(QR_CID, new ByteArrayResource(qrBytes), "image/png");
        }

        mailSender.send(mensaje);
        log.info("Email enviado a {}: {}", destinatario, asunto);
    }

    // ─── Plantillas HTML ────────────────────────────────────────────────────

    /**
     * @param incluirQr true si el QR se ha generado correctamente y vendrá
     *                  como recurso inline. Si false, mostramos sólo el
     *                  token en texto.
     */
    private String construirHtmlConfirmacion(Reserva r, boolean incluirQr) {
        String nombreUsuario = r.getUsuario().getNombre();
        String nombrePista   = r.getPista().getNombrePista();
        String nombrePoli    = r.getPista().getPolideportivo().getNombre();
        String fecha         = r.getFechaReserva().format(FECHA_FMT);
        String horaIni       = r.getHoraInicio().toString().substring(0, 5);
        String horaFin       = r.getHoraFin().toString().substring(0, 5);
        String precio        = formatearPrecio(r.getPrecioTotal());
        String token         = r.getTokenQr() != null ? r.getTokenQr() : "-";

        String bloqueQr = incluirQr
            ? "      <div style='text-align:center;margin:24px 0;'>"
            +   "<p style='color:#444;font-size:14px;margin:0 0 12px;'>Muestra este código al llegar al polideportivo:</p>"
            +   "<img src='cid:" + QR_CID + "' alt='Código QR de la reserva' "
            +     "style='display:inline-block;width:240px;height:240px;border:1px solid #e0e0e0;border-radius:8px;padding:12px;background:#ffffff;' />"
            +   "<p style='color:#999;font-size:11px;margin:12px 0 0;line-height:1.4;'>"
            +     "Si no ves la imagen en tu cliente de correo, este es tu token:<br>"
            +     "<span style='font-family:monospace;font-size:11px;color:#666;word-break:break-all;'>" + escape(token) + "</span>"
            +   "</p>"
            + "</div>"
            : "      <p style='color:#444;font-size:14px;line-height:1.5;margin:24px 0 8px;'>Tu código de acceso:</p>"
            + "      <p style='background:#0a0a0c;color:#1a9fff;font-family:monospace;font-size:13px;padding:14px;border-radius:6px;text-align:center;word-break:break-all;margin:0;'>"
            +   escape(token)
            + "</p>";

        return ""
            + "<!DOCTYPE html><html lang='es'><body style='margin:0;padding:0;background:#f4f4f7;font-family:Arial,Helvetica,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f4f4f7;padding:40px 20px;'><tr><td align='center'>"
            + "  <table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.08);'>"
            + "    <tr><td style='background:#0a0a0c;padding:32px;text-align:center;'>"
            + "      <h1 style='color:#1a9fff;margin:0;font-size:32px;letter-spacing:3px;'>BALL-ON</h1>"
            + "    </td></tr>"
            + "    <tr><td style='padding:32px;'>"
            + "      <h2 style='color:#0a0a0c;margin:0 0 16px;'>¡Reserva confirmada!</h2>"
            + "      <p style='color:#444;font-size:16px;line-height:1.5;'>Hola <strong>" + escape(nombreUsuario) + "</strong>,</p>"
            + "      <p style='color:#444;font-size:16px;line-height:1.5;'>Hemos confirmado tu reserva. Estos son los detalles:</p>"
            + "      <table width='100%' cellpadding='12' cellspacing='0' style='background:#f7f9fc;border-left:4px solid #1a9fff;border-radius:8px;margin:24px 0;'>"
            + filaDetalle("Polideportivo", nombrePoli)
            + filaDetalle("Pista", nombrePista)
            + filaDetalle("Fecha", fecha)
            + filaDetalle("Horario", horaIni + " - " + horaFin)
            + filaDetalle("Precio", precio)
            + "      </table>"
            + bloqueQr
            + "      <p style='color:#666;font-size:14px;line-height:1.5;margin-top:28px;'>Recuerda llegar unos minutos antes y presentar el código al personal del polideportivo. Puedes cancelar la reserva hasta 24h antes desde tu área personal.</p>"
            + "    </td></tr>"
            + "    <tr><td style='background:#fafafa;padding:20px;text-align:center;color:#999;font-size:12px;'>"
            + "      Este correo se ha generado automáticamente. No respondas a este mensaje.<br>&copy; Ball-On"
            + "    </td></tr>"
            + "  </table>"
            + "</td></tr></table></body></html>";
    }

    private String construirHtmlCancelacion(Reserva r) {
        String nombreUsuario = r.getUsuario().getNombre();
        String nombrePista   = r.getPista().getNombrePista();
        String nombrePoli    = r.getPista().getPolideportivo().getNombre();
        String fecha         = r.getFechaReserva().format(FECHA_FMT);
        String horaIni       = r.getHoraInicio().toString().substring(0, 5);
        String horaFin       = r.getHoraFin().toString().substring(0, 5);

        boolean reembolso = r.getEstadoPago() != null
            && "Reembolsado".equals(r.getEstadoPago().name());

        String mensajeReembolso = reembolso
            ? "<p style='color:#444;font-size:15px;line-height:1.5;background:#eaf7ee;border-left:4px solid #00ff82;padding:12px 16px;border-radius:6px;'>"
              + "Hemos iniciado el reembolso del importe al método de pago original. Puede tardar unos días hábiles en aparecer."
              + "</p>"
            : "";

        return ""
            + "<!DOCTYPE html><html lang='es'><body style='margin:0;padding:0;background:#f4f4f7;font-family:Arial,Helvetica,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f4f4f7;padding:40px 20px;'><tr><td align='center'>"
            + "  <table width='600' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 12px rgba(0,0,0,0.08);'>"
            + "    <tr><td style='background:#0a0a0c;padding:32px;text-align:center;'>"
            + "      <h1 style='color:#1a9fff;margin:0;font-size:32px;letter-spacing:3px;'>BALL-ON</h1>"
            + "    </td></tr>"
            + "    <tr><td style='padding:32px;'>"
            + "      <h2 style='color:#0a0a0c;margin:0 0 16px;'>Reserva cancelada</h2>"
            + "      <p style='color:#444;font-size:16px;line-height:1.5;'>Hola <strong>" + escape(nombreUsuario) + "</strong>,</p>"
            + "      <p style='color:#444;font-size:16px;line-height:1.5;'>Tu reserva ha sido cancelada correctamente. Estos eran los datos:</p>"
            + "      <table width='100%' cellpadding='12' cellspacing='0' style='background:#f7f9fc;border-left:4px solid #ff4d4d;border-radius:8px;margin:24px 0;'>"
            + filaDetalle("Polideportivo", nombrePoli)
            + filaDetalle("Pista", nombrePista)
            + filaDetalle("Fecha", fecha)
            + filaDetalle("Horario", horaIni + " - " + horaFin)
            + "      </table>"
            + mensajeReembolso
            + "      <p style='color:#666;font-size:14px;line-height:1.5;margin-top:24px;'>Si esta cancelación no la hiciste tú, ponte en contacto con el polideportivo cuanto antes.</p>"
            + "    </td></tr>"
            + "    <tr><td style='background:#fafafa;padding:20px;text-align:center;color:#999;font-size:12px;'>"
            + "      Este correo se ha generado automáticamente. No respondas a este mensaje.<br>&copy; Ball-On"
            + "    </td></tr>"
            + "  </table>"
            + "</td></tr></table></body></html>";
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private String filaDetalle(String etiqueta, String valor) {
        return "<tr>"
            + "<td style='color:#666;font-size:14px;'>" + escape(etiqueta) + "</td>"
            + "<td style='color:#0a0a0c;font-weight:bold;text-align:right;'>" + escape(valor) + "</td>"
            + "</tr>";
    }

    private String formatearPrecio(Double precio) {
        if (precio == null) return "-";
        return String.format(ES, "%.2f EUR", precio);
    }

    /**
     * Escapa los caracteres más problemáticos para HTML. Como los datos vienen
     * de BD (nombres de usuario, pistas, etc.), evita que se cuele cualquier
     * cosa rara y rompa el render.
     */
    private String escape(String texto) {
        if (texto == null) return "";
        return texto
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
