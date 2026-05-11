package com.ballon.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Reseña;

@Repository
public interface ReseñaRepository extends JpaRepository<Reseña, Long> {

    List<Reseña> findByPistaIdPista(Long pistaId);
    List<Reseña> findByUsuarioIdUsuario(Long usuarioId);
    
    @Query("SELECT r FROM Reseña r WHERE r.pista.polideportivo.idPolideportivo = :idPolideportivo ORDER BY r.fecha DESC")
    List<Reseña> findByPolideportivoId(@Param("idPolideportivo") Long idPolideportivo);
	
}
