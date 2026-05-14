package com.ballon.backend.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import com.ballon.backend.models.enums.EstadoPago;
import com.ballon.backend.models.enums.EstadoReserva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDTO {

	private Long idReserva;
    
    private Long idUsuario;
    private String nombreUsuarioCompleto; // Útil para administradores
    
    private Long idPista;
    private String nombrePista;
    private String nombrePolideportivo;

    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Double precioTotal;

    private EstadoReserva estadoReserva;
    private EstadoPago estadoPago;
    private String tokenQr;
    
    // Indicador si tiene reseña (para no devolver el objeto entero si no es necesario)
    private boolean tieneResena; 
    
    private boolean requierePago;
}
