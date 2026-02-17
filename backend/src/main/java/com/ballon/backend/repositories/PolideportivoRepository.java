package com.ballon.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Polideportivo;

@Repository
public interface PolideportivoRepository extends JpaRepository<Polideportivo, Long> {
	
	
}
