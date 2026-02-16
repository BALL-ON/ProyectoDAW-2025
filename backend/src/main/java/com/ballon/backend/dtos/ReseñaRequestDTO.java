package com.ballon.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class ReseñaRequestDTO {

	@NotNull(message = "La reserva es obligatoria")
    private Long idReserva;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(1) @Max(5)
    private Integer puntuacion;
    
    private String comentario;
}
