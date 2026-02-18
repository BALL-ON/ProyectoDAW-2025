package com.ballon.backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.ReservaNotFoundException;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.enums.EstadoReserva;
import com.ballon.backend.repositories.ReservaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {

	private final ReservaRepository reservaRepository;

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdUsuario(usuarioId);
    }

    /*
     * Metodo para crear reserva
     * Antes de su creacion se comprueba que la fecha no es pasada y que la pista esta disponible
     */
    public Reserva crearReserva(Reserva reserva) {
        
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

        reserva.setEstadoReserva(EstadoReserva.Confirmada);
        
        return reservaRepository.save(reserva);
    }
    
    /*
     * Metodo para cancelar una reserva. No se elimina, sino se cambia su estado a "cancelada"
     */
    public void cancelarReserva(Long id) {
    	
    	Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException(id));

        reserva.setEstadoReserva(EstadoReserva.Cancelada);

        reservaRepository.save(reserva);
    }
    
    
}
