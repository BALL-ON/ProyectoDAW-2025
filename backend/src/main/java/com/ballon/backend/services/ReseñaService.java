package com.ballon.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballon.backend.dtos.ReseñaRequestDTO;
import com.ballon.backend.dtos.ReseñaResponseDTO;
import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.mapper.ReseñaMapper;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Reseña;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.ReservaRepository;
import com.ballon.backend.repositories.ReseñaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReseñaService {

	
	private final ReseñaRepository reseñaRepository;
	private final ReseñaMapper reseñaMapper;
	private final ReservaRepository reservaRepository;

	/*
	 * Método para publicar una reseña, antes de publicarla se valida que el usuario ha reservado esa pista
	 */
	@Transactional
	public ReseñaResponseDTO publicarReseña(ReseñaRequestDTO request, Usuario usuario) {
        
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
            .orElseThrow(() -> new RuntimeException("No se encontró la reserva con ID: " + request.getIdReserva()));

        if (reserva.getReseña() != null) {
            throw new BadRequestException("Esta reserva ya ha sido valorada. No puedes valorarla de nuevo.");
        }
        
        Reseña nuevaReseña = new Reseña();
        
        // Asignamos todo lo necesario
        nuevaReseña.setPista(reserva.getPista()); 
        nuevaReseña.setUsuario(usuario);
        nuevaReseña.setReserva(reserva);
        nuevaReseña.setPuntuacion(request.getPuntuacion());
        nuevaReseña.setComentario(request.getComentario());

        // Guardamos en la base de datos
        Reseña guardada = reseñaRepository.save(nuevaReseña);
        
        return reseñaMapper.toReseñaResponse(guardada);
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
    
    /**
     * Método para hacer visible o no una reseña (por control de comentarios ofensivos)
     * @param idResena
     * @param visible
     * @return
     */
    public ReseñaResponseDTO cambiarVisibilidad(Long idResena, boolean visible) {
        Reseña resena = reseñaRepository.findById(idResena)
            .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
            
        resena.setVisible(visible);
        Reseña guardada = reseñaRepository.save(resena);
        
        return reseñaMapper.toReseñaResponse(guardada);
    }

    public List<ReseñaResponseDTO> obtenerPorPolideportivo(Long idPolideportivo) {
        List<Reseña> resenas = reseñaRepository.findByPolideportivoId(idPolideportivo);
        return resenas.stream()
                .map(reseñaMapper::toReseñaResponse)
                .collect(Collectors.toList());
    }
}
