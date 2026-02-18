package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.exception.ConflictException;
import com.ballon.backend.exception.PolideportivoDuplicatedException;
import com.ballon.backend.exception.PolideportivoNotFoundException;
import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.repositories.PistaRepository;
import com.ballon.backend.repositories.PolideportivoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolideportivoService {
	
	private final PolideportivoRepository polideportivoRepository;
	private final PistaRepository pistaRepository;
	
	public List<Polideportivo> listarTodos() {
        return polideportivoRepository.findAll();
    }

    public Polideportivo buscarPorId(Long id) {
        return polideportivoRepository.findById(id)
                .orElseThrow(() -> new PolideportivoNotFoundException(id));
    }
    
    /*
     * Metodo para guardar un polideportivo
     * Antes se comprueba que no exista otro con ese nombre
     */
    public Polideportivo guardar(Polideportivo poli) {
        if (polideportivoRepository.existsByNombre(poli.getNombre())) {
            throw new PolideportivoDuplicatedException(poli);
        }
        return polideportivoRepository.save(poli);
    }

    
    /*
     * Metodo para eliminar un polideportivo.
     * Antes se compueba que no tenga ninguna pista asociada.
     */
    public void eliminar(Long id) {
    	if (!polideportivoRepository.existsById(id)) {
            throw new PolideportivoNotFoundException(id);
        }
    	
        boolean tienePistas = !pistaRepository.findByPolideportivoIdPolideportivo(id).isEmpty();
        if (tienePistas) {
            throw new ConflictException("No se puede eliminar: este centro tiene pistas asociadas.");
        }
        polideportivoRepository.deleteById(id);
    }

}
