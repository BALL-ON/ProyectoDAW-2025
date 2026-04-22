package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.services.PolideportivoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para gestionar los centros polideportivos.
 */
@RestController
@RequestMapping("/api/polideportivos")
@RequiredArgsConstructor
public class PolideportivoController {
	
	// Inyeccion del servicio que tiene la logica de negocio
    private final PolideportivoService polideportivoService;

    /**
     * Obtener la lista completa de polideportivos.
     */
    @GetMapping
    public ResponseEntity<List<Polideportivo>> listarTodos() {
        List<Polideportivo> polis = polideportivoService.listarTodos();
        return ResponseEntity.ok(polis);
    }

    /**
     * Buscar un polideportivo específico por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Polideportivo> obtenerPorId(@PathVariable Long id) {
        Polideportivo poli = polideportivoService.buscarPorId(id);
        return ResponseEntity.ok(poli);
    }

    /**
     * Crear un nuevo polideportivo.
     * En SecurityConfig configuramos que esto sea solo para ADMIN.
     */
    @PostMapping
    public ResponseEntity<Polideportivo> crear(@RequestBody Polideportivo polideportivo) {
        Polideportivo nuevoPoli = polideportivoService.guardar(polideportivo);
        return new ResponseEntity<>(nuevoPoli, HttpStatus.CREATED);
    }

    /**
     * Actualizar los datos de un polideportivo existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Polideportivo> actualizar(@PathVariable Long id, @RequestBody Polideportivo polideportivo) {
        // Seteamos el ID del path al objeto para asegurar que editamos el correcto
        polideportivo.setIdPolideportivo(id);
        Polideportivo actualizado = polideportivoService.guardar(polideportivo);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar un polideportivo.
     * El servicio impide borrar si hay pistas dentro.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        polideportivoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
