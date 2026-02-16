package com.ballon.backend.dtos;

import com.ballon.backend.models.enums.MetodoPago;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class PolideportivoRequestDTO {

	@NotBlank private String nombre;
    @NotBlank private String direccion;
    @NotBlank private String poblacion;
    private MetodoPago metodoPagoPreferido;
}
