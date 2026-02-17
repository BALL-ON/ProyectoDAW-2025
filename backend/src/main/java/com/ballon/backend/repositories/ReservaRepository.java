package com.ballon.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	List<Reserva> findByUsuarioIdUsuario(Long usuarioId);
    List<Reserva> findByPistaIdPista(Long pistaId);
	
}
