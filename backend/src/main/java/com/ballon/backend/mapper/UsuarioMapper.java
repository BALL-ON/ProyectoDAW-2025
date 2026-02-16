package com.ballon.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ballon.backend.dtos.UsuarioRequestDTO;
import com.ballon.backend.dtos.UsuarioResponseDTO;
import com.ballon.backend.dtos.UsuarioUpdateDTO;
import com.ballon.backend.models.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

	@Mapping(source = "polideportivoAsignado.idPolideportivo", target = "idPolideportivoAsignado")
    UsuarioResponseDTO toResponse(Usuario usuario);

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "puntosPenalizacion", constant = "0")
    @Mapping(target = "bloqueadoHasta", ignore = true)
    @Mapping(target = "polideportivoAsignado", ignore = true)
    @Mapping(target = "reseñas", ignore = true)
    Usuario toEntity(UsuarioRequestDTO request);

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "puntosPenalizacion", ignore = true)
    @Mapping(target = "bloqueadoHasta", ignore = true)
    @Mapping(target = "polideportivoAsignado", ignore = true)
    @Mapping(target = "reseñas", ignore = true)
    void updateEntityFromDto(UsuarioUpdateDTO dto, @MappingTarget Usuario usuario);
}
