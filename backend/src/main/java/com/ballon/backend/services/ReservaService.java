package com.ballon.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballon.backend.dtos.OcupacionSlotDTO;
import com.ballon.backend.dtos.ReservaRequestDTO;
import com.ballon.backend.dtos.ReservaResponseDTO;
import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.ReservaNotFoundException;
import com.ballon.backend.mapper.ReservaMapper;
import com.ballon.backend.models.Pista;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.EstadoPago;
import com.ballon.backend.models.enums.EstadoReserva;
import com.ballon.backend.repositories.PistaRepository;
import com.ballon.backend.repositories.ReservaRepository;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio del módulo de reservas (CU-02 reservar, CU-03 cancelar).
 *
 * Reglas implementadas:
 *  - Antelación mínima de 1 día (coincide con la validación del frontend).
 *  - Máximo 3 horas seguidas por reserva.
 *  - Sin solapamiento con reservas Confirmadas en la misma pista.
 *  - El precio se calcula y persiste en el momento de la reserva
 *    (RI_03: el precio_total debe persistir para evitar cambios de tarifa).
 *  - El usuario sólo puede cancelar reservas propias y con 24h de antelación.
 *  - Tras confirmar o cancelar, se envía email transaccional al usuario.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private static final int MAX_HORAS_RESERVA = 3;
    private static final int HORAS_MINIMAS_CANCELACION = 24;

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;
    private final ReservaMapper reservaMapper;
    private final EmailService emailService;

    // ─── Lecturas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuario(Long idUsuario) {
        return reservaMapper.toResponseList(
                reservaRepository.findByUsuarioIdUsuario(idUsuario));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuarioYPista(Long idUsuario, Long idPista) {
        return reservaMapper.toResponseList(
                reservaRepository.findByUsuarioIdUsuarioAndPistaIdPista(idUsuario, idPista));
    }

    /**
     * Devuelve los rangos ocupados (sólo horaInicio/horaFin) de una pista en
     * una fecha. Lo consume el frontend para deshabilitar slots.
     */
    @Transactional(readOnly = true)
    public List<OcupacionSlotDTO> obtenerOcupacion(Long idPista, LocalDate fecha) {
        return reservaMapper.toOcupacionList(
                reservaRepository.findConfirmadasByPistaAndFecha(idPista, fecha));
    }

    // ─── Crear (CU-02) ───────────────────────────────────────────────────────

    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto, String username) {

        // 1. Resolver usuario autenticado y pista
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        Pista pista = pistaRepository.findById(dto.getIdPista())
                .orElseThrow(() -> new BadRequestException(
                        "No existe la pista con id " + dto.getIdPista()));

        // 2. Validaciones de negocio
        validarHorario(dto);
        validarSinSolapamiento(dto);

        // 3. Cálculo del precio (precio/hora * horas reservadas)
        double horas = ChronoUnit.MINUTES.between(dto.getHoraInicio(), dto.getHoraFin()) / 60.0;
        double precioTotal = pista.getPrecioHora() * horas;

        // 4. Construir y persistir entidad
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setPista(pista);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFin(dto.getHoraFin());
        reserva.setPrecioTotal(precioTotal);
        reserva.setEstadoReserva(EstadoReserva.Confirmada);
        reserva.setEstadoPago(EstadoPago.Pendiente);
        reserva.setTokenQr(UUID.randomUUID().toString());

        Reserva guardada = reservaRepository.save(reserva);

        // 5. Notificación por email (async, fire-and-forget).
        // Va al final: si fallara antes algún paso, no habríamos confirmado
        // por email algo que no llegó a guardarse.
        emailService.enviarConfirmacionReserva(guardada);

        return reservaMapper.toResponse(guardada);
    }

    // ─── Cancelar (CU-03) ────────────────────────────────────────────────────

    public void cancelarReserva(Long idReserva, String username) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        // Sólo el dueño puede cancelar
        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes permiso para cancelar una reserva que no es tuya.");
        }

        // No se cancela algo ya cancelado o finalizado
        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException(
                    "Sólo se pueden cancelar reservas en estado Confirmada.");
        }

        // Política de cancelación: 24h de margen
        LocalDateTime inicioReserva = LocalDateTime.of(
                reserva.getFechaReserva(), reserva.getHoraInicio());
        if (inicioReserva.isBefore(LocalDateTime.now().plusHours(HORAS_MINIMAS_CANCELACION))) {
            throw new BadRequestException(
                    "No puedes cancelar con menos de " + HORAS_MINIMAS_CANCELACION + " horas de antelación.");
        }

        reserva.setEstadoReserva(EstadoReserva.Cancelada);

        // Si estaba pagada, marcamos como reembolsada.
        // El reembolso real contra la pasarela queda pendiente de integración (RF_05).
        if (reserva.getEstadoPago() == EstadoPago.Pagado) {
            reserva.setEstadoPago(EstadoPago.Reembolsado);
        }

        Reserva cancelada = reservaRepository.save(reserva);

        // Notificación por email. El EmailService detecta si el estadoPago es
        // Reembolsado e incluye el aviso de reembolso en el cuerpo del mensaje.
        emailService.enviarCancelacionReserva(cancelada);
    }

    // ─── Validaciones privadas ───────────────────────────────────────────────

    private void validarHorario(ReservaRequestDTO dto) {
        LocalTime ini = dto.getHoraInicio();
        LocalTime fin = dto.getHoraFin();

        if (!ini.isBefore(fin)) {
            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin.");
        }

        long minutos = ChronoUnit.MINUTES.between(ini, fin);
        if (minutos > MAX_HORAS_RESERVA * 60L) {
            throw new BadRequestException(
                    "No puedes reservar más de " + MAX_HORAS_RESERVA + " horas seguidas.");
        }

        // Antelación mínima de 1 día (coincide con minDate del frontend).
        // OJO: el @FutureOrPresent del DTO permitiría reservar HOY; esta regla
        // es más estricta y prevalece. Si quieres permitir hoy, elimina este if
        // y deja que la valide @FutureOrPresent.
        if (dto.getFechaReserva().isBefore(LocalDate.now().plusDays(1))) {
            throw new BadRequestException("Debes reservar con al menos 1 día de antelación.");
        }
    }

    private void validarSinSolapamiento(ReservaRequestDTO dto) {
        List<Reserva> conflictos = reservaRepository.findSolapamientos(
                dto.getIdPista(),
                dto.getFechaReserva(),
                dto.getHoraInicio(),
                dto.getHoraFin());

        if (!conflictos.isEmpty()) {
            throw new BadRequestException(
                    "Ya hay una reserva confirmada que se solapa con ese horario.");
        }
    }

    // ─── Check-in mediante QR ────────────────────────────────────────────────

    public void realizarCheckIn(String tokenQr) {
        Reserva reserva = reservaRepository.findByTokenQr(tokenQr)
                .orElseThrow(() -> new BadRequestException("Código QR inválido"));

        // Comprobar que sea una reserva confirmada
        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException("Esta reserva no está en estado Confirmada (Actual: " + reserva.getEstadoReserva() + ").");
        }

        // Comprobar que la reserva sea para hoy
        if (!reserva.getFechaReserva().equals(LocalDate.now())) {
            throw new BadRequestException("Esta reserva no es para hoy. Es para el " + reserva.getFechaReserva());
        }

        // Si todo va bien, se escanea el QR
        reserva.setEstadoReserva(EstadoReserva.Disfrutada);
        reservaRepository.save(reserva);
    }


    // ─── Tarea Automática (Control de No Asistidos) ──────────────────────────

    /**
     * Se ejecuta automáticamente cada 15 minutos (900000 milisegundos).
     * Revisa si hay reservas que ya pasaron su margen de 30 minutos sin hacer check-in.
     */
    @Scheduled(fixedRate = 900000)
    public void procesarNoAsistidos() {
        LocalDate hoy = LocalDate.now();

        // Calculamos la hora actual menos 30 minutos
        LocalTime horaLimite = LocalTime.now().minusMinutes(30);

        // Buscamos las de hoy que ya pasaron el límite de 30 mins
        List<Reserva> caducadasHoy = reservaRepository.findConfirmadasCaducadasHoy(hoy, horaLimite);
        for (Reserva r : caducadasHoy) {
            r.setEstadoReserva(EstadoReserva.No_Asistido);
        }
        reservaRepository.saveAll(caducadasHoy);

        // Buscamos las de días anteriores por seguridad
        List<Reserva> colgadasAyer = reservaRepository.findConfirmadasDiasAnteriores(hoy);
        for (Reserva r : colgadasAyer) {
            r.setEstadoReserva(EstadoReserva.No_Asistido);
        }
        reservaRepository.saveAll(colgadasAyer);

        if (!caducadasHoy.isEmpty() || !colgadasAyer.isEmpty()) {
            System.out.println("CRON: Se han marcado " + (caducadasHoy.size() + colgadasAyer.size()) + " reservas como No_Asistido.");
        }
    }
}
