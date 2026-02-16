package com.ballon.backend.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class ReseñaResponseDTO {

	private Long idReseña;
    private Long idReserva;
    private String nombreUsuario; // "Juan P." (privacidad)
    private String nombrePista;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
}
