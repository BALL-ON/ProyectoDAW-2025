package com.ballon.backend.dtos;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO ligero para devolver SOLO los rangos ocupados de una pista
 * en un día concreto, sin exponer información personal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcupacionSlotDTO {

    private LocalTime horaInicio;
    private LocalTime horaFin;
}
