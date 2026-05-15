package com.ballon.backend.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Genera códigos QR como PNG en memoria.
 *
 * Lo usa el EmailService para embeber el QR de check-in en los correos de
 * confirmación. Si en el futuro se añade un endpoint GET /reservas/{id}/qr
 * o se necesita el QR en otro sitio, también puede consumirse desde aquí.
 */
@Service
public class QrCodeService {

    /** Lado en píxeles del QR generado. 300px va bien para email y para imprimir. */
    private static final int LADO_PX = 300;

    /** Color de los módulos oscuros (ARGB). Negro corporativo Ball-On. */
    private static final int COLOR_MODULO = 0xFF0A0A0C;

    /** Color del fondo (ARGB). Blanco puro para máxima legibilidad al escanear. */
    private static final int COLOR_FONDO = 0xFFFFFFFF;

    /**
     * Genera un PNG con el QR del contenido dado.
     *
     * @param contenido texto a codificar (en nuestro caso, el tokenQr de la reserva)
     * @return bytes del PNG listos para adjuntar o servir
     */
    public byte[] generarPng(String contenido) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = Map.of(
            // M = recupera hasta el 15% del QR aunque haya manchas, dobleces, etc.
            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
            // Margen pequeno: queda mas compacto y se ve mejor en email
            EncodeHintType.MARGIN, 1
        );

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(
            contenido, BarcodeFormat.QR_CODE, LADO_PX, LADO_PX, hints
        );

        MatrixToImageConfig config = new MatrixToImageConfig(COLOR_MODULO, COLOR_FONDO);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, "PNG", out, config);
            return out.toByteArray();
        }
    }
}
