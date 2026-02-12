package com.ballon.backend.models;

import java.util.List;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tipo_pista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoPista {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_pista")
    private Long idTipoPista;

    @Column(name = "nombre_tipo", nullable = false, length = 50)
    private String nombreTipo;

    private String descripcion;
    
    @OneToMany(mappedBy = "tipoPista", fetch = FetchType.LAZY)
    @JsonIgnore @ToString.Exclude
    private List<Pista> pistas;

}
