package com.ballon.backend.models;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;

import com.ballon.backend.models.enums.DiaSemana;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "horario_pista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioPista {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHorario;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pista", nullable = false)
    private Pista pista;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
}
