package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.TipoPista;
import com.ballon.backend.repositories.TipoPistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPistaService {

	private final TipoPistaRepository tipoPistaRepository;

    public List<TipoPista> listarTipos() {
        return tipoPistaRepository.findAll();
    }
    
    /*
     * Metodo para crear un tipo de pista.
     * Antes se combrueba que no haya una creada con el mismo nombre
     */
    public TipoPista crearTipo(TipoPista tipo) {

        if (tipoPistaRepository.existsByNombreTipo(tipo.getNombreTipo())) {
            throw new RuntimeException("Este tipo de pista ya está creda.");
        }
        return tipoPistaRepository.save(tipo);
    }
}
