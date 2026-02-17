package com.ballon.backend.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.ballon.backend.models.enums.EstadoPago;
import com.ballon.backend.models.enums.EstadoReserva;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pista", nullable = false)
    private Pista pista;

    private LocalDate fechaReserva;
    
    private LocalTime horaInicio;
    
    private LocalTime horaFin;
    
    private Double precioTotal;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva = EstadoReserva.Confirmada;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago = EstadoPago.Pendiente;

    @Column(unique = true)
    private String tokenQr; // El token que leerá el QR 

    private String pagoId; // Para la pasarela de pago

    @Column(updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Reseña reseña;

}
