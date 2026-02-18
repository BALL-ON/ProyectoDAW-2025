package com.ballon.backend.dtos;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class PistaRequestDTO {

	@NotNull(message = "El polideportivo es obligatorio")
    private Long idPolideportivo;

    @NotNull(message = "El tipo de pista es obligatorio")
    private Long idTipoPista;

    @NotBlank(message = "El nombre de la pista es obligatorio")
    private String nombrePista;

    @Min(1)
    private Integer capacidad;
    private Double precioHora;
    private Boolean requierePagoPrevio;
    private Integer tiempoMinCancelacionHoras;
    private Boolean activa;
}
