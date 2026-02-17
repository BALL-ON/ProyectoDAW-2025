package com.ballon.backend.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Pista;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {
	
	List<Pista> findByPolideportivoId(Long polideportivoId);
	
	
}
