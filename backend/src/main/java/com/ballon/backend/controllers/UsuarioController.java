package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.dtos.UsuarioUpdateDTO;
import com.ballon.backend.mapper.UsuarioMapper;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	/*
	 * Inyeccion del servicio y mapper
	 */
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    // Ver mi propio perfil (sacado del token)
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        // Extraemos el email del token
        String emailDelToken = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Buscamos la entidad en la base de datos
        Usuario usuario = usuarioService.buscarPorEmail(emailDelToken);
        
        // Traducimos la Entidad a DTO para ocultar la contraseña al frontend
        return ResponseEntity.ok(usuarioMapper.toResponse(usuario));
    }

    // Listar todos los usuarios (Solo para ADMIN)
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }
    
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(@RequestBody UsuarioUpdateDTO updateDTO) {
        String emailDelToken = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuarioResponseDTO perfilActualizado = usuarioService.actualizarPerfil(emailDelToken, updateDTO); // llamamos al servicio para que lo actualice
        
        return ResponseEntity.ok(perfilActualizado);
    }
}
