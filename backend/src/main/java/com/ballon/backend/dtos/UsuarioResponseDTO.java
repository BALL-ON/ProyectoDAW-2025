package com.ballon.backend.dtos;

import java.time.LocalDateTime;

import com.ballon.backend.models.enums.Rol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

	private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private Rol rol;
    private Integer puntosPenalizacion;
    private LocalDateTime bloqueadoHasta;
    // Solo devolvemos el ID del polideportivo si es relevante
    private Long idPolideportivoAsignado; 
}
