package com.ballon.backend.models;

import java.time.LocalDateTime;
import java.util.List;

import com.ballon.backend.models.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = true)
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @Column(name = "puntos_penalizacion")
    private Integer puntosPenalizacion = 0;

    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude // Para que Lombok no entre en bucle infinito
    private List<Reserva> reservas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_polideportivo_asignado")
    private Polideportivo polideportivoAsignado;
    
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude
    private List<Reseña> reseñas;

}
