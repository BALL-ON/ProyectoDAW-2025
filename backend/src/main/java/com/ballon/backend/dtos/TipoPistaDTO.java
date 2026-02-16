package com.ballon.backend.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class TipoPistaDTO {

	private Long idTipoPista;
    private String nombreTipo;
    private String descripcion;
}
