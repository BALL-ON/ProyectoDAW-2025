package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    // Propiedad que Angular leerá como response.token
    private String token;
    private String rol;
}
