package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO {

	private String nombre;
    private String apellidos;
    private String telefono;
    // La contraseña se suele manejar en un endpoint separado o aquí como opcional
}
