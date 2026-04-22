package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.TipoPista;
import com.ballon.backend.services.TipoPistaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-pista")
@RequiredArgsConstructor
public class TipoPistaController {

    private final TipoPistaService tipoPistaService;

    // Listar todos los tipos de deporte disponibles
    @GetMapping
    public ResponseEntity<List<TipoPista>> listarTodos() {
        return ResponseEntity.ok(tipoPistaService.listarTipos());
    }

    // Crear un nuevo deporte
    @PostMapping
    public ResponseEntity<TipoPista> crear(@RequestBody TipoPista tipo) {
        return new ResponseEntity<>(tipoPistaService.crearTipo(tipo), HttpStatus.CREATED);
    }
}
