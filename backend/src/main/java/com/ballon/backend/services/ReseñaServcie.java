package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ballon.backend.models.Reseña;
import com.ballon.backend.repositories.ReservaRepository;
import com.ballon.backend.repositories.ReseñaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReseñaServcie {

	
	private final ReseñaRepository reseñaRepository;
	private final ReservaRepository reservaRepository;

	/*
	 * Método para publicar una reseña, antes de publicarla se valida que el usuario ha reservado esa pista
	 */
    public Reseña publicarReseña(Reseña reseña) {
    	boolean haReservado = reservaRepository.existsByUsuarioIdUsuarioAndPistaIdPista(
                reseña.getUsuario().getIdUsuario(), 
                reseña.getPista().getIdPista()
            );

            if (!haReservado) {
                throw new RuntimeException("No puedes valorar una pista en la que no has jugado.");
            }

            return reseñaRepository.save(reseña);
    }

    /*
     * Método para mostrar las opiniones de cada pista
     */
    public List<Reseña> listarPorPista(Long pistaId) {
        return reseñaRepository.findByPistaIdPista(pistaId);
    }
}
