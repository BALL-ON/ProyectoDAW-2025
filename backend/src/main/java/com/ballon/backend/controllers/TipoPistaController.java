package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.TipoPistaDTO;
import com.ballon.backend.services.TipoPistaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-pista")
@RequiredArgsConstructor
public class TipoPistaController {

    private final TipoPistaService tipoPistaService;

    /** Listar todos los tipos de deporte disponibles (público, filtros y selectores). */
    @GetMapping
    public ResponseEntity<List<TipoPistaDTO>> listarTodos() {
        return ResponseEntity.ok(tipoPistaService.listarTipos());
    }

    /** Crear un nuevo tipo de deporte (Admin Global). */
    @PostMapping
    public ResponseEntity<TipoPistaDTO> crear(@Valid @RequestBody TipoPistaDTO dto) {
        return new ResponseEntity<>(tipoPistaService.crearTipo(dto), HttpStatus.CREATED);
    }
}