package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.ReservaService;
import com.ballon.backend.services.UsuarioService; // Asegúrate de tenerlo
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    @GetMapping("/mis-reservas")
    public ResponseEntity<List<Reserva>> listarMisReservas() { 
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuario.getIdUsuario()));
    }

    @PostMapping
    public ResponseEntity<Reserva> reservar(@RequestBody Reserva reserva) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(reservaService.crearReserva(reserva, username), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reservaService.cancelarReserva(id, username);
        return ResponseEntity.noContent().build();
    }
}