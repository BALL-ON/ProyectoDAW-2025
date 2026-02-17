package com.ballon.backend.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "pista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pista {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pista")
    private Long idPista;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_polideportivo", nullable = false)
    private Polideportivo polideportivo;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_pista", nullable = false)
    private TipoPista tipoPista;

    @Column(name = "nombre_pista", nullable = false, length = 50)
    private String nombrePista;

    private Integer capacidad = 4;

    @Column(name = "precio_hora")
    private Double precioHora;

    @Column(name = "requiere_pago_previo")
    private Boolean requierePagoPrevio = false;

    @Column(name = "tiempo_min_cancelacion_horas")
    private Integer tiempoMinCancelacionHoras = 24;
    
    @OneToMany(mappedBy = "pista", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Reserva> reservas;
    
    @OneToMany(mappedBy = "pista", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude
    private List<HorarioPista> horarios;
}
