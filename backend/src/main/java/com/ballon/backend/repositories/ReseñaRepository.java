package com.ballon.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Reseña;

@Repository
public interface ReseñaRepository extends JpaRepository<Reseña, Long> {

    List<Reseña> findByPistaId(Long pistaId);
	
}
