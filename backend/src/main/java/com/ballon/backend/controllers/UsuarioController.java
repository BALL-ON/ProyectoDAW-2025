package com.ballon.backend.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ballon.backend.dtos.CambiarPasswordDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.dtos.UsuarioUpdateDTO;
import com.ballon.backend.mapper.UsuarioMapper;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.repositories.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

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
    
    /**
     * Endpoint para sacar la foto del perfil de usuario
     * @param authentication
     * @return
     */
    @GetMapping(value = "/mi-foto", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> obtenerMiFoto(Authentication authentication) {
        String email = authentication.getName();
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        if (usuario.getFotoPerfil() == null || usuario.getFotoPerfil().length == 0) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(usuario.getFotoPerfil());
    }
    
    /**
     * Endpoint para cambiar foto de perfil
     * @param authentication
     * @param nuevaFoto
     * @return
     */
    @PutMapping(value = "/mi-foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> actualizarMiFoto(
            Authentication authentication,
            @RequestPart("foto") MultipartFile nuevaFoto) {
        
        try {
            String email = authentication.getName();

            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setFotoPerfil(nuevaFoto.getBytes());

            usuarioRepository.save(usuario);
            
            return ResponseEntity.ok(Map.of("mensaje", "Foto actualizada con éxito"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al guardar la nueva foto"));
        }
    }
    
    /**
     * Endpoint para cambiar la contraseña
     * @param request
     * @param authentication
     * @return
     */
    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordDTO request, Authentication authentication) {
        
        try {
            String email = authentication.getName();
            usuarioService.cambiarPassword(email, request);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada con éxito"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error interno al cambiar la contraseña"));
        }
    }
}
