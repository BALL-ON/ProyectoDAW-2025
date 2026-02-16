package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class PistaResponseDTO {

	private Long idPista;
    private String nombrePista;
    private Integer capacidad;
    private Double precioHora;
    private Boolean requierePagoPrevio;
    private Integer tiempoMinCancelacionHoras;
    
    // Relaciones aplanadas para facilitar lectura en frontend
    private Long idPolideportivo;
    private String nombrePolideportivo;
    private Long idTipoPista;
    private String nombreTipoPista;
}
