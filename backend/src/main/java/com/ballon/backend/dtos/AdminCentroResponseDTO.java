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
public class AdminCentroResponseDTO {
    
    private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private LocalDateTime bloqueadoHasta;
    
    // Datos del polideportivo asignado
    private Long idPolideportivo;
    private String nombrePolideportivo; 
}
