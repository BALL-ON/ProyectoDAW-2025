package com.ballon.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.MensajeContacto;


@Repository
public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {

	
	
}
