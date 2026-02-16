package com.ballon.backend.dtos;

import java.time.LocalTime;

import com.ballon.backend.models.enums.DiaSemana;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class HorarioDTO {

	private Long idHorario;
    private Long idPista;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
