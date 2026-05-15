package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordDTO {
	private String contrasenaActual;
    private String nuevaContrasena;

}
