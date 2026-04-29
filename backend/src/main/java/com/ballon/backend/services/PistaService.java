package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.exception.PistaNotFoundException;
import com.ballon.backend.models.Pista;
import com.ballon.backend.repositories.PistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PistaService {
	
	private final PistaRepository pistaRepository;

	/**
     * Muestra todas las pistas
     */
    public List<Pista> listarTodas() {
        return pistaRepository.findAll();
    }
    
    /**
     * Muestra todas las pistas ACTIVAS de un polideportivo concreto
     */
    public List<Pista> listarActivasPorPolideportivo(Long polideportivoId) {

        return pistaRepository.findByPolideportivoIdPolideportivo(polideportivoId)
                .stream()
                .filter(p -> p.isActiva())
                .toList();
    }

    /**
     * Busca una pista por su ID
     */
    public Pista buscarPorId(Long id) {
        return pistaRepository.findById(id)
                .orElseThrow(() -> new PistaNotFoundException(id));
    }

    /**
     * Guarda una pista validando que no haya otra con el mismo nombre 
     * en el mismo polideportivo.
     */
    public Pista guardar(Pista pista) {
        
        if (pista.getPolideportivo() != null) {
            pistaRepository.findByNombrePistaAndPolideportivo_IdPolideportivo(
                pista.getNombrePista(), 
                pista.getPolideportivo().getIdPolideportivo()
            ).ifPresent(p -> {
                // Si estamos creando o editando una pista distinta con el mismo nombre
                if (pista.getIdPista() == null || !p.getIdPista().equals(pista.getIdPista())) {
                    throw new RuntimeException("La pista '" + pista.getNombrePista() + 
                        "' ya existe en este polideportivo.");
                }
            });
        }

        return pistaRepository.save(pista);
    }

    /**
     * Cambia el estado de una pista (Activa/Inactiva).
     * Se usa cuando una pista está en mantenimiento
     */
    public Pista cambiarEstadoPista(Long id, boolean activa) {
        // Buscamos la pista primero
        Pista pista = pistaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró la pista con ID: " + id));
        
        pista.setActiva(activa);
        return pistaRepository.save(pista);
    }

    /**
     * Elimina una pista por su ID.
     */
    public void eliminar(Long id) {
        if (!pistaRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: No existe la pista con ID " + id);
        }
        pistaRepository.deleteById(id);
    }

}
