package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.Pista;
import com.ballon.backend.services.PistaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pistas")
@RequiredArgsConstructor
public class PistaController {

    private final PistaService pistaService;

    // Ver todas las pistas
    @GetMapping
    public ResponseEntity<List<Pista>> listarTodas() {
        return ResponseEntity.ok(pistaService.listarTodas());
    }

    // buscar pistas por polideportivos
    @GetMapping("/polideportivo/{idPolideportivo}")
    public ResponseEntity<List<Pista>> listarPorPolideportivo(@PathVariable Long idPolideportivo) {
        return ResponseEntity.ok(pistaService.listarActivasPorPolideportivo(idPolideportivo));
    }

    // Crear pista (Admin)
    @PostMapping
    public ResponseEntity<Pista> crear(@RequestBody Pista pista) {
        return new ResponseEntity<>(pistaService.guardar(pista), HttpStatus.CREATED);
    }

    // Cambiar estado (activar/desactivar por mantenimiento)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Pista> cambiarEstado(@PathVariable Long id, @RequestParam boolean activa) {
        return ResponseEntity.ok(pistaService.cambiarEstadoPista(id, activa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
