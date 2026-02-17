package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.Pista;
import com.ballon.backend.repositories.PistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PistaService {
	
	private final PistaRepository pistaRepository;

    public List<Pista> listarTodas() {
        return pistaRepository.findAll();
    }
    
    public List<Pista> listarActivasPorPolideportivo(Long polideportivoId) {

        return pistaRepository.findByPolideportivoIdPolideportivo(polideportivoId)
                .stream()
                .filter(p -> p.isActiva())
                .toList();
    }

    public Pista buscarPorId(Long id) {
        return pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));
    }

}
