package com.ballon.backend.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.HorarioPista;


@Repository
public interface HorarioPistaRepository extends JpaRepository<HorarioPista, Long> {

	List<HorarioPista> findByPistaId(Long pistaId);
	
}
