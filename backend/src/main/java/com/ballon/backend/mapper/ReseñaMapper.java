package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.ReseñaRequestDTO;
import com.ballon.backend.dtos.ReseñaResponseDTO;
import com.ballon.backend.models.Reseña;
import com.ballon.backend.models.Usuario;

@Mapper(componentModel = "spring")
public interface ReseñaMapper {

	@Mapping(source = "idReseña", target = "idResena")
	@Mapping(source = "reserva.idReserva", target = "idReserva")
    @Mapping(source = "usuario", target = "nombreUsuario")
    @Mapping(source = "pista.nombrePista", target = "nombrePista")
    @Mapping(source = "fecha", target = "fecha")
    ReseñaResponseDTO toReseñaResponse(Reseña reseña);

	@Mapping(source = "idReserva", target = "reserva.idReserva")
    @Mapping(target = "idReseña", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pista", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "visible", ignore = true)
    Reseña toReseñaEntity(ReseñaRequestDTO request);
    
    default String mapearNombrePrivado(Usuario usuario) {
        if (usuario == null) {
            return "Cliente Anónimo";
        }
        
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "Cliente";
        String apellidos = usuario.getApellidos();
        
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            // Coge el nombre + espacio + primera letra del apellido + punto
            return nombre + " " + apellidos.trim().charAt(0) + ".";
        }
        
        return nombre;
    }

}
