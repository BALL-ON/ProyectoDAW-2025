package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.PistaRequestDTO;
import com.ballon.backend.dtos.PistaResponseDTO;
import com.ballon.backend.services.PistaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pistas")
@RequiredArgsConstructor
public class PistaController {

    private final PistaService pistaService;


    /** Listado completo (admin). */
    @GetMapping
    public ResponseEntity<List<PistaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(pistaService.listarTodas());
    }

    /** Pistas ACTIVAS de un polideportivo. Lo consume la página /polideportivos/:id/pistas. */
    @GetMapping("/polideportivo/{idPolideportivo}")
    public ResponseEntity<List<PistaResponseDTO>> listarPorPolideportivo(
            @PathVariable Long idPolideportivo) {
        return ResponseEntity.ok(pistaService.listarActivasPorPolideportivo(idPolideportivo));
    }

    /** Detalle de una pista. */
    @GetMapping("/{id}")
    public ResponseEntity<PistaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pistaService.buscarPorId(id));
    }

    /** Crear pista (sólo ADMIN según SecurityConfig). */
    @PostMapping
    public ResponseEntity<PistaResponseDTO> crear(@Valid @RequestBody PistaRequestDTO dto) {
        return new ResponseEntity<>(pistaService.crear(dto), HttpStatus.CREATED);
    }

    /** Actualizar pista. */
    @PutMapping("/{id}")
    public ResponseEntity<PistaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PistaRequestDTO dto) {
        return ResponseEntity.ok(pistaService.actualizar(id, dto));
    }

    /** Activar/desactivar pista (mantenimiento). */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PistaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activa) {
        return ResponseEntity.ok(pistaService.cambiarEstadoPista(id, activa));
    }

    /** Eliminar pista. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
