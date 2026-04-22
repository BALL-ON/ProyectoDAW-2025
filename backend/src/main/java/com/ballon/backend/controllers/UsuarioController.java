package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Ver mi propio perfil (sacado del token)
    @GetMapping("/perfil")
    public ResponseEntity<Usuario> obtenerPerfil() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(usuarioService.buscarPorUsername(username));
    }

    // Listar todos los usuarios (Solo para ADMIN)
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }
}
