package com.ballon.backend.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "reseña")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reseña {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReseña;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pista", nullable = false)
    private Pista pista;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reserva", unique = true)
    private Reserva reserva;

    private Integer puntuacion;
    
    private String comentario;

    @Column(updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

}
