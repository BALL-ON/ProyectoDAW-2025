package com.ballon.backend.repositories;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Pista;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {
	
	List<Pista> findByPolideportivoIdPolideportivo(Long polideportivoId);
	
	Optional<Pista> findByNombreAndPolideportivo_IdPolideportivo(String nombre, Long idPolideportivo);
	
	
}
