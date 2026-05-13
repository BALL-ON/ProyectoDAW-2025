package com.ballon.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballon.backend.dtos.HorarioDTO;
import com.ballon.backend.mapper.HorarioMapper;
import com.ballon.backend.services.HorarioPistaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioPistaController {

    private final HorarioPistaService horarioService;
    private final HorarioMapper horarioMapper;

    /**
     * Devuelve los horarios semanales configurados para una pista
     * (un registro por día de la semana en el que la pista abre).
     * Lo usa el frontend para generar la rejilla de slots de reserva
     * en función del día elegido.
     */
    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<HorarioDTO>> listarPorPista(@PathVariable Long pistaId) {
        List<HorarioDTO> horarios = horarioMapper.toHorarioDtoList(
            horarioService.listarPorPista(pistaId)
        );
        return ResponseEntity.ok(horarios);
    }
}
