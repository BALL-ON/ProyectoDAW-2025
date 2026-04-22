package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.Reseña;
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
    public ResponseEntity<List<Reseña>> listarPorPista(@PathVariable Long pistaId) {
        return ResponseEntity.ok(reseñaService.listarPorPista(pistaId));
    }

    @PostMapping
    public ResponseEntity<Reseña> publicar(@RequestBody Reseña reseña) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        
        reseña.setUsuario(usuario);
        
        return new ResponseEntity<>(reseñaService.publicarReseña(reseña), HttpStatus.CREATED);
    }
}
