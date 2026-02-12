package com.ballon.backend.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mensaje_contacto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeContacto {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = true)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String asunto;

    @Column(nullable = false, length = 255)
    private String mensaje;

    @Column(name = "fecha_envio", updatable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean leido = false;
}
