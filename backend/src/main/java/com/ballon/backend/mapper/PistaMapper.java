package com.ballon.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ballon.backend.dtos.PistaRequestDTO;
import com.ballon.backend.dtos.PistaResponseDTO;
import com.ballon.backend.models.Pista;

/**
 * Mapper de Pista ↔ DTOs.
 *
 * NOTA: NO se incluye un toPistaEntity(PistaRequestDTO) que mapee
 * idPolideportivo → polideportivo.idPolideportivo. Eso crearía entidades
 * "fantasma" no gestionadas por JPA. En el service cargamos polideportivo
 * y tipoPista con findById y construimos la Pista a mano.
 */
@Mapper(componentModel = "spring")
public interface PistaMapper {

    @Mapping(source = "polideportivo.idPolideportivo", target = "idPolideportivo")
    @Mapping(source = "polideportivo.nombre", target = "nombrePolideportivo")
    @Mapping(source = "tipoPista.idTipoPista", target = "idTipoPista")
    @Mapping(source = "tipoPista.nombreTipo", target = "nombreTipoPista")
    PistaResponseDTO toResponse(Pista pista);

    List<PistaResponseDTO> toResponseList(List<Pista> pistas);

    /**
     * Aplica los campos editables del DTO sobre una entidad existente.
     * NO toca relaciones (polideportivo, tipoPista, reservas, horarios)
     * — esas se gestionan a mano en el service si hace falta.
     */
    @Mapping(target = "idPista", ignore = true)
    @Mapping(target = "polideportivo", ignore = true)
    @Mapping(target = "tipoPista", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    void updateEntityFromDto(PistaRequestDTO request, @MappingTarget Pista entidad);
}
