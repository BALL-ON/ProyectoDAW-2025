package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.TipoPistaDTO;
import com.ballon.backend.models.TipoPista;

@Mapper(componentModel = "spring")
public interface TipoPistaMapper {

	TipoPistaDTO toTipoPistaDto(TipoPista tipoPista);
	
    @Mapping(target = "pistas", ignore = true)
    TipoPista toTipoPistaEntity(TipoPistaDTO dto);
}
