package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.HorarioPista;
import com.ballon.backend.repositories.HorarioPistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HorarioPistaService {

	private final HorarioPistaRepository horarioPistaRepository;

    public List<HorarioPista> listarPorPista(Long pistaId) {
        return horarioPistaRepository.findByPistaIdPista(pistaId);  //todos los horarios que hay para una pista concreta
    }
    
    public HorarioPista buscarPorId(Long id) {
        return horarioPistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
    }
}
