package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.ReservaRequestDTO;
import com.ballon.backend.dtos.ReservaResponseDTO;
import com.ballon.backend.models.Reserva;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

	@Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombreUsuarioCompleto") 
    @Mapping(source = "pista.idPista", target = "idPista")
    @Mapping(source = "pista.nombrePista", target = "nombrePista")
    @Mapping(source = "pista.polideportivo.nombre", target = "nombrePolideportivo")
    // Se asume que esta lógica se calcula en el Service o mediante una consulta
    @Mapping(target = "tieneReseña", ignore = true) 
    ReservaResponseDTO toResponse(Reserva reserva);

	@Mapping(source = "idUsuario", target = "usuario.idUsuario")
    @Mapping(source = "idPista", target = "pista.idPista")
    @Mapping(target = "idReserva", ignore = true)
    @Mapping(target = "tokenQr", ignore = true)
    @Mapping(target = "pagoId", ignore = true)
    @Mapping(target = "estadoReserva", ignore = true)
    @Mapping(target = "estadoPago", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
	@Mapping(target = "fechaCreacion", ignore = true)
	@Mapping(target = "reseña", ignore = true)
    Reserva toEntity(ReservaRequestDTO request);
}
