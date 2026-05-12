package com.ballon.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ballon.backend.dtos.MensajeContactoDTO;
import com.ballon.backend.models.MensajeContacto;

@Mapper(componentModel = "spring")
public interface MensajeContactoMapper {

    MensajeContactoDTO toMensajeDto(MensajeContacto mensaje);

    /** MapStruct genera el bucle automáticamente reutilizando toMensajeDto. */
    List<MensajeContactoDTO> toMensajeDtoList(List<MensajeContacto> mensajes);

    /**
     * Convierte el DTO entrante en entidad para guardar.
     * Ignoramos los campos auto-gestionados (id auto-generado, fecha de
     * envío gestionada por la entidad y flag de leído gestionado por el
     * service) para que el cliente no pueda manipularlos enviándolos
     * en el body de la petición.
     */
    @Mapping(target = "idMensaje", ignore = true)
    @Mapping(target = "fechaEnvio", ignore = true)
    @Mapping(target = "leido", ignore = true)
    MensajeContacto toMensajeEntity(MensajeContactoDTO dto);
}
