package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.PolideportivoRequestDTO;
import com.ballon.backend.dtos.PolideportivoResponseDTO;
import com.ballon.backend.models.Polideportivo;

@Mapper(componentModel = "spring")
public interface PolideporivoMapper {

	 PolideportivoResponseDTO toPolideportivoDto(Polideportivo polideportivo);
	
	 @Mapping(target = "idPolideportivo", ignore = true)
	 @Mapping(target = "pistas", ignore = true)
	 @Mapping(target = "administradores", ignore = true)
	 Polideportivo toPolideportivoEntity(PolideportivoRequestDTO request);

}
