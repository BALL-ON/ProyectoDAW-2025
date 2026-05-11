package com.ballon.backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.OcupacionSlotDTO;
import com.ballon.backend.dtos.ReservaRequestDTO;
import com.ballon.backend.dtos.ReservaResponseDTO;
import com.ballon.backend.dtos.TokenQrDTO;
import com.ballon.backend.models.Usuario;
import com.ballon.backend.services.ReservaService;
import com.ballon.backend.services.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioService usuarioService;

    /**
     * Devuelve las reservas del usuario autenticado.
     * Útil para el "Historial" del perfil y para la página de reserva.
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservas() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuario.getIdUsuario()));
    }

    /**
     * Devuelve las reservas del usuario autenticado FILTRADAS por pista.
     * Lo usa la página /reserva/:idPista para pintar el historial de esa pista.
     */
    @GetMapping("/mis-reservas/pista/{idPista}")
    public ResponseEntity<List<ReservaResponseDTO>> listarMisReservasPorPista(@PathVariable Long idPista) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(reservaService.listarPorUsuarioYPista(usuario.getIdUsuario(), idPista));
    }

    /**
     * NUEVO: Devuelve los slots OCUPADOS de una pista en una fecha concreta.
     * No expone datos personales: solo horaInicio y horaFin.
     * Usado por el grid de slots para deshabilitar las horas reservadas.
     */
    @GetMapping("/pista/{idPista}/ocupacion")
    public ResponseEntity<List<OcupacionSlotDTO>> ocupacionDelDia(
            @PathVariable Long idPista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(reservaService.obtenerOcupacion(idPista, fecha));
    }

    /**
     * Crea una reserva. Recibe DTO (no la entidad), valida con @Valid
     * y devuelve un DTO limpio.
     */
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> reservar(@Valid @RequestBody ReservaRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ReservaResponseDTO creada = reservaService.crearReserva(dto, username);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    /**
     * Cancela una reserva propia (CU-03).
     * El servicio se encarga de validar que la reserva pertenezca al usuario
     * y que cumpla la política de cancelación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        reservaService.cancelarReserva(id, username);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Endpoint usado por el administrador/lector para escanear el QR.
     * Cambia el estado a "Disfrutada".
     */
    @PostMapping("/check-in")
    public ResponseEntity<String> escanearQr(@RequestBody TokenQrDTO dto) {
        reservaService.realizarCheckIn(dto.token);
        return ResponseEntity.ok("Check-in realizado correctamente. Reserva Disfrutada.");
    }
    
}
