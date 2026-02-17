package com.ballon.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.TipoPista;

@Repository
public interface TipoPistaRepository extends JpaRepository<TipoPista, Long> {

	
}
