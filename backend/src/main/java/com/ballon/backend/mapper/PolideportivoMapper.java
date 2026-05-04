package com.ballon.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ballon.backend.dtos.PolideportivoRequestDTO;
import com.ballon.backend.dtos.PolideportivoResponseDTO;
import com.ballon.backend.models.Polideportivo;

/**
 * Mapper entre Polideportivo y sus DTOs.
 *
 * NOTA: se renombra el archivo (antes "PolideporivoMapper", faltaba la 't').
 * Si tenías referencias al nombre antiguo, sustitúyelas en tus imports.
 */
@Mapper(componentModel = "spring")
public interface PolideportivoMapper {

    PolideportivoResponseDTO toResponse(Polideportivo polideportivo);

    List<PolideportivoResponseDTO> toResponseList(List<Polideportivo> polideportivos);

    @Mapping(target = "idPolideportivo", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "administradores", ignore = true)
    Polideportivo toEntity(PolideportivoRequestDTO request);

    /**
     * Actualiza una entidad existente con los datos del DTO.
     * Útil para PUT: cargas la entidad de BBDD y le aplicas los cambios
     * sin tocar las relaciones.
     */
    @Mapping(target = "idPolideportivo", ignore = true)
    @Mapping(target = "pistas", ignore = true)
    @Mapping(target = "administradores", ignore = true)
    void updateEntityFromDto(PolideportivoRequestDTO request, @MappingTarget Polideportivo entidad);
}
