package com.ballon.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.HorarioDTO;
import com.ballon.backend.dtos.HorarioRequestDTO;
import com.ballon.backend.models.HorarioPista;

@Mapper(componentModel = "spring")
public interface HorarioMapper {

    @Mapping(source = "pista.idPista", target = "idPista")
    HorarioDTO toHorarioDto(HorarioPista horario);

    /** MapStruct genera automáticamente el bucle reutilizando toHorarioDto. */
    List<HorarioDTO> toHorarioDtoList(List<HorarioPista> horarios);

    @Mapping(source = "idPista", target = "pista.idPista")
    @Mapping(target = "idHorario", ignore = true)
    HorarioPista toHorarioEntity(HorarioRequestDTO request);
}
