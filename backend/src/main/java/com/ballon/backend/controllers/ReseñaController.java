package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.ReseñaRequestDTO;
import com.ballon.backend.dtos.ReseñaResponseDTO;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.ReseñaService;
import com.ballon.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ReseñaController {

    private final ReseñaService reseñaService;
    private final UsuarioService usuarioService;

    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<ReseñaResponseDTO>> listarPorPista(@PathVariable Long pistaId) {
        return ResponseEntity.ok(reseñaService.listarPorPista(pistaId));
    }
    
    @GetMapping("/mis-resenas")
    public ResponseEntity<List<ReseñaResponseDTO>> obtenerMisResenas() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        List<ReseñaResponseDTO> misResenas = reseñaService.listarResenasPorUsuario(usuario.getIdUsuario());
        
        return ResponseEntity.ok(misResenas);
    }

    @PostMapping
    public ResponseEntity<ReseñaResponseDTO> publicar(@RequestBody ReseñaRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        
        // Le pasamos el DTO de entrada y el Usuario al Service para que haga la lógica
        ReseñaResponseDTO reseñaPublicada = reseñaService.publicarReseña(request, usuario);
        
        return new ResponseEntity<>(reseñaPublicada, HttpStatus.CREATED);
    }
    
    /** PATCH /api/resenas/{id}/visibilidad?visible=false (ADMIN, moderación) */
    @PatchMapping("/{id}/visibilidad")
    public ResponseEntity<ReseñaResponseDTO> cambiarVisibilidad(@PathVariable Long id, @RequestParam boolean visible) {
        return ResponseEntity.ok(reseñaService.cambiarVisibilidad(id, visible));
    }
    
    
    /**
     * Endpoint para obtener las reseñas de un polideportivo especifico
     * @param idPolideportivo
     * @return
     */
    @GetMapping("/polideportivo/{idPolideportivo}")
    public ResponseEntity<List<ReseñaResponseDTO>> obtenerResenasDelCentro(@PathVariable Long idPolideportivo) {
        // Llama al servicio que use el repositorio de arriba y devuelva la lista
        return ResponseEntity.ok(reseñaService.obtenerPorPolideportivo(idPolideportivo));
    }
}
