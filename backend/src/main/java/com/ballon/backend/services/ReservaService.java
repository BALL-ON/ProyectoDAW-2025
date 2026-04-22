package com.ballon.backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.ReservaNotFoundException;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.models.enums.EstadoReserva;
import com.ballon.backend.repositories.ReservaRepository;
import com.ballon.backend.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final ReservaRepository reservaRepository;
	private final UsuarioRepository usuarioRepository;

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdUsuario(usuarioId);
    }

    /*
     * Metodo para crear reserva modificado:
     * Ahora recibe el username del token para asignar el dueño real
     */
    public Reserva crearReserva(Reserva reserva, String username) {
        
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (reserva.getFechaReserva().isBefore(LocalDate.now())) {
            throw new BadRequestException("No puedes reservar en una fecha que ya ha pasado.");
        }

        boolean ocupada = reservaRepository.existsByPistaIdPistaAndFechaReservaAndHoraInicio(
                reserva.getPista().getIdPista(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio()
        );

        if (ocupada) {
            throw new BadRequestException("Lo sentimos, esta pista ya está reservada para ese horario.");
        }

        reserva.setUsuario(usuario);
        reserva.setEstadoReserva(EstadoReserva.Confirmada);
        
        return reservaRepository.save(reserva);
    }
    
    /*
     * Metodo para cancelar que comprueba que el que cancela es el dueño de la reserva
     */
    public void cancelarReserva(Long id, String username) {
        
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException(id));

        if (!reserva.getUsuario().getUsername().equals(username)) {
            throw new BadRequestException("No tienes permiso para cancelar una reserva que no es tuya.");
        }

        reserva.setEstadoReserva(EstadoReserva.Cancelada);
        reservaRepository.save(reserva);
    }
    
    
}
