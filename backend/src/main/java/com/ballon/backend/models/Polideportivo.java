package com.ballon.backend.models;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.ballon.backend.models.enums.MetodoPago;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "polideportivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Polideportivo {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_polideportivo")
    private Long idPolideportivo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String poblacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago_preferido")
    private MetodoPago metodoPagoPreferido = MetodoPago.Presencial;
    
    @OneToMany(mappedBy = "polideportivo", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude
    private List<Pista> pistas;

    @OneToMany(mappedBy = "polideportivoAsignado", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude
    private List<Usuario> administradores;
    
}
