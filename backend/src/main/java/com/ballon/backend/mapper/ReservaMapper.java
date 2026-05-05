package com.ballon.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.ballon.backend.dtos.OcupacionSlotDTO;
import com.ballon.backend.dtos.ReservaResponseDTO;
import com.ballon.backend.models.Reserva;
import com.ballon.backend.models.Usuario;

/**
 * Mapper de Reserva → DTOs.
 *
 * NOTA: He quitado el método toEntity(ReservaRequestDTO).
 * Razón: mapear idUsuario/idPista a entidades vacías Usuario/Pista con sólo el
 * id genera entidades NO gestionadas por JPA, y al persistirlas Hibernate no
 * carga las relaciones reales (precio/hora, polideportivo, etc.).
 * En el ReservaService cargamos las entidades con findById y construimos
 * la Reserva manualmente.
 */
@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(source = "usuario.idUsuario", target = "idUsuario")
    @Mapping(source = "usuario", target = "nombreUsuarioCompleto", qualifiedByName = "nombreCompleto")
    @Mapping(source = "pista.idPista", target = "idPista")
    @Mapping(source = "pista.nombrePista", target = "nombrePista")
    @Mapping(source = "pista.polideportivo.nombre", target = "nombrePolideportivo")
    @Mapping(target = "tieneReseña", expression = "java(reserva.getReseña() != null)")
    ReservaResponseDTO toResponse(Reserva reserva);

    List<ReservaResponseDTO> toResponseList(List<Reserva> reservas);

    // ─── OcupacionSlotDTO ────────────────────────────────────────────────────

    OcupacionSlotDTO toOcupacion(Reserva reserva);

    List<OcupacionSlotDTO> toOcupacionList(List<Reserva> reservas);

    // ─── Helpers ─────────────────────────────────────────────────────────────

    @Named("nombreCompleto")
    default String nombreCompleto(Usuario u) {
        if (u == null) return null;
        String nombre = u.getNombre() != null ? u.getNombre() : "";
        String apellidos = u.getApellidos() != null ? u.getApellidos() : "";
        return (nombre + " " + apellidos).trim();
    }
}
