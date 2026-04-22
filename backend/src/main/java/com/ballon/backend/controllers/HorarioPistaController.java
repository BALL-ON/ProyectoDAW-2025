package com.ballon.backend.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ballon.backend.models.HorarioPista;
import com.ballon.backend.services.HorarioPistaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioPistaController {

    private final HorarioPistaService horarioService;

    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<HorarioPista>> listarPorPista(@PathVariable Long pistaId) {
        return ResponseEntity.ok(horarioService.listarPorPista(pistaId));
    }
}