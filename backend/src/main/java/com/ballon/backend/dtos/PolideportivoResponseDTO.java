package com.ballon.backend.dtos;

import com.ballon.backend.models.enums.MetodoPago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class PolideportivoResponseDTO {

	private Long idPolideportivo;
    private String nombre;
    private String direccion;
    private String poblacion;
    private MetodoPago metodoPagoPreferido;
}
