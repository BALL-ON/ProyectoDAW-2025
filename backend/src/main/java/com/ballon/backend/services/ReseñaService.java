package com.ballon.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ballon.backend.dtos.ReseñaRequestDTO;
import com.ballon.backend.dtos.ReseñaResponseDTO;
import com.ballon.backend.mapper.ReseñaMapper;
import com.ballon.backend.models.Reseña;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.ReseñaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReseñaService {

	
	private final ReseñaRepository reseñaRepository;
	private final ReseñaMapper reseñaMapper;

	/*
	 * Método para publicar una reseña, antes de publicarla se valida que el usuario ha reservado esa pista
	 */
    public ReseñaResponseDTO publicarReseña(ReseñaRequestDTO request, Usuario usuario) {
        
        Reseña reseña = reseñaMapper.toReseñaEntity(request);
        reseña.setUsuario(usuario);
        Reseña reseñaGuardada = reseñaRepository.save(reseña);
        
        return reseñaMapper.toReseñaResponse(reseñaGuardada);
    }
    
    /*
     * Método para listar todas las reseñas de un usuario
     */
	public List<ReseñaResponseDTO> listarResenasPorUsuario(Long idUsuario) {
	        
	        List<Reseña> listaEntidades = reseñaRepository.findByUsuarioIdUsuario(idUsuario);
	
	        return listaEntidades.stream()
	            .map(reseñaMapper::toReseñaResponse)
	            .collect(Collectors.toList());
	    }

    /*
     * Método para mostrar las opiniones de cada pista
     */    
    public List<ReseñaResponseDTO> listarPorPista(Long pistaId) {
        List<Reseña> reseñas = reseñaRepository.findByPistaIdPista(pistaId);
        
        return reseñas.stream()
                .map(reseñaMapper::toReseñaResponse)
                .collect(Collectors.toList());
    }
}
