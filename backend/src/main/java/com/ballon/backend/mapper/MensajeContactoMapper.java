package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.MensajeContactoDTO;
import com.ballon.backend.models.MensajeContacto;

@Mapper(componentModel = "spring")
public interface MensajeContactoMapper {

	MensajeContactoDTO toMensajeDto(MensajeContacto mensaje);
    
    @Mapping(target = "fechaEnvio", ignore = true)
    MensajeContacto toMensajeEntity(MensajeContactoDTO dto);
}
