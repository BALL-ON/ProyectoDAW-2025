package com.ballon.backend.dtos;

import java.time.LocalTime;

import com.ballon.backend.models.enums.DiaSemana;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class HorarioRequestDTO {

	@NotNull 
	private Long idPista;
	
    @NotNull 
    private DiaSemana diaSemana;
    
    @NotNull 
    private LocalTime horaInicio;
    
    @NotNull 
    private LocalTime horaFin;
}
