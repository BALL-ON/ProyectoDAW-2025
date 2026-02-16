package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.PistaRequestDTO;
import com.ballon.backend.dtos.PistaResponseDTO;
import com.ballon.backend.models.Pista;

@Mapper(componentModel = "spring")
public interface PistaMapper {

	@Mapping(source = "polideportivo.idPolideportivo", target = "idPolideportivo")
    @Mapping(source = "polideportivo.nombre", target = "nombrePolideportivo")
    @Mapping(source = "tipoPista.idTipoPista", target = "idTipoPista")
    @Mapping(source = "tipoPista.nombreTipo", target = "nombreTipoPista")
    PistaResponseDTO toPistaDto(Pista pista);

	@Mapping(source = "idPolideportivo", target = "polideportivo.idPolideportivo")
    @Mapping(source = "idTipoPista", target = "tipoPista.idTipoPista")
    @Mapping(target = "idPista", ignore = true)
    @Mapping(target = "horarios", ignore = true)
	@Mapping(target = "reservas", ignore = true)
    Pista toPistaEntity(PistaRequestDTO request);

}
