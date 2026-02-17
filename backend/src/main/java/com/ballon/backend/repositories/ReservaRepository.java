package com.ballon.backend.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	List<Reserva> findByUsuarioIdUsuario(Long usuarioId);
	
    List<Reserva> findByPistaIdPista(Long pistaId);
    
	boolean existsByUsuarioIdUsuarioAndPistaIdPista(Long idUsuario, Long idPista);
	
	boolean existsByPistaIdPistaAndFechaReservaAndHoraInicio(Long idPista, LocalDate fechaReserva, LocalTime horaInicio);
	
}
