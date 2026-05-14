package com.ballon.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;
import com.ballon.backend.dtos.QrReservaDTO;

import com.ballon.backend.dtos.OcupacionSlotDTO;
import com.ballon.backend.dtos.PagoRequestDTO;
import com.ballon.backend.dtos.ReservaRequestDTO;
import com.ballon.backend.dtos.ReservaResponseDTO;
import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.ReservaNotFoundException;
import com.ballon.backend.mapper.ReservaMapper;
import com.ballon.backend.models.Pista;
import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.EstadoPago;
import com.ballon.backend.models.enums.EstadoReserva;
import com.ballon.backend.models.enums.MetodoPago;
import com.ballon.backend.repositories.PistaRepository;
import com.ballon.backend.repositories.ReservaRepository;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

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
    private final QrCodeService qrCodeService;

    // ─── Lecturas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuario(Long idUsuario) {
        List<Reserva> reservas = reservaRepository.findByUsuarioIdUsuario(idUsuario);
        List<ReservaResponseDTO> lista = reservaMapper.toResponseList(reservas);
        return enriquecerConRequierePago(lista, reservas);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuarioYPista(Long idUsuario, Long idPista) {
        List<Reserva> reservas = reservaRepository.findByUsuarioIdUsuarioAndPistaIdPista(idUsuario, idPista);
        List<ReservaResponseDTO> lista = reservaMapper.toResponseList(reservas);
        return enriquecerConRequierePago(lista, reservas);
    }
    
    /**
     * Setea el campo requierePago en cada DTO recorriendo las entidades en paralelo.
     * Es necesario porque MapStruct no puede calcular ese flag (depende de la pista
     * y polideportivo asociados, no de un campo directo de Reserva).
     */
    private List<ReservaResponseDTO> enriquecerConRequierePago(
            List<ReservaResponseDTO> dtos, List<Reserva> reservas) {
        for (int i = 0; i < dtos.size(); i++) {
            ReservaResponseDTO dto = dtos.get(i);
            Reserva r = reservas.get(i);
            dto.setRequierePago(requierePagoOnline(r.getPista())
                    && r.getEstadoPago() == EstadoPago.Pendiente);
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<OcupacionSlotDTO> obtenerOcupacion(Long idPista, LocalDate fecha) {
        return reservaMapper.toOcupacionList(
                reservaRepository.findConfirmadasByPistaAndFecha(idPista, fecha));
    }

    /**
     * NUEVO. Devuelve una reserva concreta verificando que pertenezca al
     * usuario autenticado. Lo usa la pantalla de pago para refrescar datos.
     */
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long idReserva, String username) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes acceso a esta reserva.");
        }

        ReservaResponseDTO dto = reservaMapper.toResponse(reserva);
        dto.setRequierePago(requierePagoOnline(reserva.getPista())
                && reserva.getEstadoPago() == EstadoPago.Pendiente);
        return dto;
    }
    
    /**
     * Devuelve el código QR de una reserva 
     *
     * Validaciones:
     *  - La reserva debe pertenecer al usuario autenticado.
     *  - Debe estar en estado Confirmada.
     *  - Si la pista exige pago online, sólo se entrega si está Pagada.
     */
    @Transactional(readOnly = true)
    public QrReservaDTO obtenerQr(Long idReserva, String username) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes acceso a esta reserva.");
        }

        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException(
                    "Esta reserva no está en estado Confirmada (Actual: "
                            + reserva.getEstadoReserva() + ").");
        }

        if (requierePagoOnline(reserva.getPista())
                && reserva.getEstadoPago() != EstadoPago.Pagado) {
            throw new BadRequestException(
                    "Debes pagar la reserva antes de obtener el código QR.");
        }

        try {
            byte[] png = qrCodeService.generarPng(reserva.getTokenQr());
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(png);
            return new QrReservaDTO(base64, reserva.getTokenQr());
        } catch (Exception e) {
            throw new BadRequestException("No se pudo generar el código QR.");
        }
    }

    // ─── Crear (CU-02) ───────────────────────────────────────────────────────

    public ReservaResponseDTO crearReserva(ReservaRequestDTO dto, String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        Pista pista = pistaRepository.findById(dto.getIdPista())
                .orElseThrow(() -> new BadRequestException(
                        "No existe la pista con id " + dto.getIdPista()));

        validarHorario(dto);
        validarSinSolapamiento(dto);

        double horas = ChronoUnit.MINUTES.between(dto.getHoraInicio(), dto.getHoraFin()) / 60.0;
        double precioTotal = pista.getPrecioHora() * horas;

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

        boolean requierePago = requierePagoOnline(pista);

        // Si la pista exige pago online, NO mandamos email de confirmación
        // todavía: la reserva está pendiente de cobro. El email se dispara
        // en procesarPago() cuando el pago sea OK. Para Gratis/Presencial
        // mantenemos el comportamiento actual (email al confirmar).
        if (!requierePago) {
            emailService.enviarConfirmacionReserva(guardada);
        }

        ReservaResponseDTO respuesta = reservaMapper.toResponse(guardada);
        respuesta.setRequierePago(requierePago);
        return respuesta;
    }

    // ─── Pagar (NUEVO) ───────────────────────────────────────────────────────

    /**
     * Procesa el pago "simulado" de una reserva.
     *
     * - Verifica que la reserva exista, sea del usuario y esté pendiente de pago.
     * - Valida la tarjeta (formato + algoritmo de Luhn).
     * - Aplica reglas estilo Stripe test para decidir éxito o fallo.
     * - Si OK: marca Pagado, asigna pagoId y dispara email de confirmación.
     * - Si KO: lanza BadRequestException con el motivo. La reserva queda Pendiente.
     */
    public ReservaResponseDTO procesarPago(Long idReserva, PagoRequestDTO dto, String username) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes permiso para pagar esta reserva.");
        }

        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException(
                    "Esta reserva no se puede pagar (estado: " + reserva.getEstadoReserva() + ").");
        }

        if (reserva.getEstadoPago() != EstadoPago.Pendiente) {
            throw new BadRequestException("Esta reserva ya está pagada o reembolsada.");
        }

        if (!requierePagoOnline(reserva.getPista())) {
            throw new BadRequestException("Esta reserva no requiere pago online.");
        }

        // Normalizamos: quitamos espacios del número
        String numero = dto.getNumeroTarjeta().replaceAll("\\s+", "");

        // Validaciones de tarjeta
        if (!numero.matches("^[0-9]{13,19}$")) {
            throw new BadRequestException("Número de tarjeta inválido.");
        }
        if (!cumpleLuhn(numero)) {
            throw new BadRequestException("Número de tarjeta inválido.");
        }
        if (!fechaCaducidadValida(dto.getMesExp(), dto.getAnioExp())) {
            throw new BadRequestException("La tarjeta está caducada.");
        }

        // Reglas de simulación (mismas tarjetas que la documentación de Stripe Test)
        switch (numero) {
            case "4000000000000002":
                throw new BadRequestException("Tarjeta rechazada por la entidad bancaria.");
            case "4000000000000069":
                throw new BadRequestException("La tarjeta está caducada.");
            case "4000000000000119":
                throw new BadRequestException("Error de procesamiento. Inténtalo de nuevo.");
            case "4000000000009995":
                throw new BadRequestException("Fondos insuficientes.");
            default:
                // Cualquier otra tarjeta Luhn-válida pasa
                break;
        }

        // Pago OK: marcamos reserva
        reserva.setEstadoPago(EstadoPago.Pagado);
        reserva.setPagoId("sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24));
        Reserva pagada = reservaRepository.save(reserva);

        // Email de confirmación (ahora sí, ya está todo confirmado y pagado)
        emailService.enviarConfirmacionReserva(pagada);

        ReservaResponseDTO respuesta = reservaMapper.toResponse(pagada);
        respuesta.setRequierePago(false);
        return respuesta;
    }

    // ─── Cancelar (CU-03) ────────────────────────────────────────────────────

    public void cancelarReserva(Long idReserva, String username) {

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNotFoundException(idReserva));

        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes permiso para cancelar una reserva que no es tuya.");
        }

        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException(
                    "Sólo se pueden cancelar reservas en estado Confirmada.");
        }

        // Regla 24h: NO aplica si la reserva nunca llegó a pagarse.
        // En ese caso permitimos cancelar siempre (es lo que hace el botón
        // "Cancelar pago" en la pasarela simulada).
        if (reserva.getEstadoPago() != EstadoPago.Pendiente) {
            LocalDateTime inicioReserva = LocalDateTime.of(
                    reserva.getFechaReserva(), reserva.getHoraInicio());
            if (inicioReserva.isBefore(LocalDateTime.now().plusHours(HORAS_MINIMAS_CANCELACION))) {
                throw new BadRequestException(
                        "No puedes cancelar con menos de " + HORAS_MINIMAS_CANCELACION + " horas de antelación.");
            }
        }

        reserva.setEstadoReserva(EstadoReserva.Cancelada);

        if (reserva.getEstadoPago() == EstadoPago.Pagado) {
            reserva.setEstadoPago(EstadoPago.Reembolsado);
        }

        Reserva cancelada = reservaRepository.save(reserva);

        // Sólo notificamos por email si la reserva había llegado a confirmarse
        // de cara al usuario (es decir, no estaba pendiente de pago).
        // Las reservas abandonadas en la pasarela no envían correo de "cancelación".
        if (cancelada.getEstadoPago() != EstadoPago.Pendiente
                || cancelada.getPagoId() != null) {
            emailService.enviarCancelacionReserva(cancelada);
        }
    }

    // ─── Helpers privados ────────────────────────────────────────────────────

    private boolean requierePagoOnline(Pista pista) {
        if (pista == null || !pista.getRequierePagoPrevio()) {
            return false;
        }
        Polideportivo poli = pista.getPolideportivo();
        return poli != null && poli.getMetodoPagoPreferido() == MetodoPago.Online;
    }

    private boolean cumpleLuhn(String numero) {
        int suma = 0;
        boolean alternar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(numero.charAt(i));
            if (alternar) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            suma += n;
            alternar = !alternar;
        }
        return suma % 10 == 0;
    }

    private boolean fechaCaducidadValida(int mes, int anio) {
        YearMonth caducidad = YearMonth.of(anio, mes);
        YearMonth ahora = YearMonth.now();
        return !caducidad.isBefore(ahora);
    }

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

        if (reserva.getEstadoReserva() != EstadoReserva.Confirmada) {
            throw new BadRequestException("Esta reserva no está en estado Confirmada (Actual: " + reserva.getEstadoReserva() + ").");
        }

        if (!reserva.getFechaReserva().equals(LocalDate.now())) {
            throw new BadRequestException("Esta reserva no es para hoy. Es para el " + reserva.getFechaReserva());
        }

        // Defensa adicional: si la pista requiere pago online y no se ha
        // pagado, no permitimos el check-in (RI_pago).
        if (requierePagoOnline(reserva.getPista())
                && reserva.getEstadoPago() != EstadoPago.Pagado) {
            throw new BadRequestException("Esta reserva está pendiente de pago. No se puede acceder.");
        }

        reserva.setEstadoReserva(EstadoReserva.Disfrutada);
        reservaRepository.save(reserva);
    }

    // ─── Cron de no asistidos ────────────────────────────────────────────────

    @Scheduled(fixedRate = 900000)
    public void procesarNoAsistidos() {
        LocalDate hoy = LocalDate.now();
        LocalTime horaLimite = LocalTime.now().minusMinutes(30);

        List<Reserva> caducadasHoy = reservaRepository.findConfirmadasCaducadasHoy(hoy, horaLimite);
        for (Reserva r : caducadasHoy) {
            r.setEstadoReserva(EstadoReserva.No_Asistido);
        }
        reservaRepository.saveAll(caducadasHoy);

        List<Reserva> colgadasAyer = reservaRepository.findConfirmadasDiasAnteriores(hoy);
        for (Reserva r : colgadasAyer) {
            r.setEstadoReserva(EstadoReserva.No_Asistido);
        }
        reservaRepository.saveAll(colgadasAyer);

        if (!caducadasHoy.isEmpty() || !colgadasAyer.isEmpty()) {
            System.out.println("CRON: Se han marcado " + (caducadasHoy.size() + colgadasAyer.size()) + " reservas como No_Asistido.");
        }
    }

    public List<ReservaResponseDTO> listarReservasPorPolideportivo(Long idPolideportivo) {
        List<Reserva> reservas = reservaRepository.findReservasByPolideportivo(idPolideportivo);
        return reservas.stream()
                .map(reservaMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<ReservaResponseDTO> obtenerReservasPaginadas(Long idPolideportivo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> paginaReservas = reservaRepository.findByPolideportivoIdPaginado(idPolideportivo, pageable);
        return paginaReservas.map(reservaMapper::toResponse);
    }
}