package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.ReseñaRequestDTO;
import com.ballon.backend.dtos.ReseñaResponseDTO;
import com.ballon.backend.models.Reseña;

@Mapper(componentModel = "spring")
public interface ReseñaMapper {

	@Mapping(source = "reserva.idReserva", target = "idReserva")
    @Mapping(source = "usuario.nombre", target = "nombreUsuario")
    @Mapping(source = "pista.nombrePista", target = "nombrePista")
    @Mapping(source = "fecha", target = "fecha")
    ReseñaResponseDTO toReseñaResponse(Reseña reseña);

    @Mapping(source = "idReserva", target = "reserva.idReserva")
    @Mapping(target = "idReseña", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "pista", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    Reseña toReseñaEntity(ReseñaRequestDTO request);

}
