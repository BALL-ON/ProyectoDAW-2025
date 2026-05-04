package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // ← OJO: este es el correcto (Spring), no el de Swagger
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.PolideportivoRequestDTO;
import com.ballon.backend.dtos.PolideportivoResponseDTO;
import com.ballon.backend.services.PolideportivoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar los centros polideportivos.
 */
@RestController
@RequestMapping("/api/polideportivos")
@RequiredArgsConstructor
public class PolideportivoController {

    private final PolideportivoService polideportivoService;

    /** Listado público de polideportivos (lo consume la home/listado del frontend). */
    @GetMapping
    public ResponseEntity<List<PolideportivoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(polideportivoService.listarTodos());
    }

    /** Detalle de un polideportivo. */
    @GetMapping("/{id}")
    public ResponseEntity<PolideportivoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(polideportivoService.buscarPorId(id));
    }

    /** Crear un polideportivo nuevo (sólo ADMIN según SecurityConfig). */
    @PostMapping
    public ResponseEntity<PolideportivoResponseDTO> crear(
            @Valid @RequestBody PolideportivoRequestDTO dto) {
        return new ResponseEntity<>(polideportivoService.crear(dto), HttpStatus.CREATED);
    }

    /** Actualizar un polideportivo existente. */
    @PutMapping("/{id}")
    public ResponseEntity<PolideportivoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PolideportivoRequestDTO dto) {
        return ResponseEntity.ok(polideportivoService.actualizar(id, dto));
    }

    /** Eliminar un polideportivo (falla si tiene pistas asociadas). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        polideportivoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
