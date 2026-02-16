package com.ballon.backend.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class MensajeContactoDTO {

	 // Sirve tanto para Request como Response, ya que es simple
    private Long idMensaje; // Nulo al crear

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String email;

    private String telefono;

    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
    
    private LocalDateTime fechaEnvio;
    private Boolean leido;
}
