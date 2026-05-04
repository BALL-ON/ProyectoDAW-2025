package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballon.backend.dtos.PistaRequestDTO;
import com.ballon.backend.dtos.PistaResponseDTO;
import com.ballon.backend.exception.BadRequestException;
import com.ballon.backend.exception.PistaNotFoundException;
import com.ballon.backend.exception.PolideportivoNotFoundException;
import com.ballon.backend.mapper.PistaMapper;
import com.ballon.backend.models.Pista;
import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.models.TipoPista;
import com.ballon.backend.repositories.PistaRepository;
import com.ballon.backend.repositories.PolideportivoRepository;
import com.ballon.backend.repositories.TipoPistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PistaService {

    private final PistaRepository pistaRepository;
    private final PolideportivoRepository polideportivoRepository;
    private final TipoPistaRepository tipoPistaRepository;
    private final PistaMapper pistaMapper;

    // ─── Lecturas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PistaResponseDTO> listarTodas() {
        return pistaMapper.toResponseList(pistaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<PistaResponseDTO> listarActivasPorPolideportivo(Long idPolideportivo) {
        // Filtrado en SQL en lugar de en memoria con .stream() — más eficiente.
        return pistaMapper.toResponseList(
                pistaRepository.findByPolideportivoIdPolideportivoAndActivaTrue(idPolideportivo));
    }

    @Transactional(readOnly = true)
    public PistaResponseDTO buscarPorId(Long id) {
        return pistaMapper.toResponse(buscarEntidadPorId(id));
    }

    /** Lookup interno de la entidad. Lo usamos en update y otros services (Reserva). */
    public Pista buscarEntidadPorId(Long id) {
        return pistaRepository.findById(id)
                .orElseThrow(() -> new PistaNotFoundException(id));
    }

    // ─── Crear ───────────────────────────────────────────────────────────────

    public PistaResponseDTO crear(PistaRequestDTO dto) {

        Polideportivo poli = polideportivoRepository.findById(dto.getIdPolideportivo())
                .orElseThrow(() -> new PolideportivoNotFoundException(dto.getIdPolideportivo()));

        TipoPista tipo = tipoPistaRepository.findById(dto.getIdTipoPista())
                .orElseThrow(() -> new BadRequestException(
                        "No existe el tipo de pista con id " + dto.getIdTipoPista()));

        // Comprobar duplicado dentro del mismo polideportivo
        pistaRepository.findByNombrePistaAndPolideportivo_IdPolideportivo(
                        dto.getNombrePista(), dto.getIdPolideportivo())
                .ifPresent(p -> {
                    throw new BadRequestException("La pista '" + dto.getNombrePista()
                            + "' ya existe en este polideportivo.");
                });

        // Construcción manual: el mapper no carga relaciones, las inyectamos aquí.
        Pista pista = new Pista();
        pista.setPolideportivo(poli);
        pista.setTipoPista(tipo);
        pista.setNombrePista(dto.getNombrePista());
        pista.setCapacidad(dto.getCapacidad() != null ? dto.getCapacidad() : 4);
        pista.setPrecioHora(dto.getPrecioHora());
        pista.setRequierePagoPrevio(
                dto.getRequierePagoPrevio() != null ? dto.getRequierePagoPrevio() : false);
        pista.setTiempoMinCancelacionHoras(
                dto.getTiempoMinCancelacionHoras() != null ? dto.getTiempoMinCancelacionHoras() : 24);
        pista.setActiva(dto.getActiva() != null ? dto.getActiva() : true);

        return pistaMapper.toResponse(pistaRepository.save(pista));
    }

    // ─── Actualizar ──────────────────────────────────────────────────────────

    public PistaResponseDTO actualizar(Long id, PistaRequestDTO dto) {

        Pista existente = buscarEntidadPorId(id);

        // Si cambia el polideportivo o el tipo, los recargamos
        if (!existente.getPolideportivo().getIdPolideportivo().equals(dto.getIdPolideportivo())) {
            Polideportivo nuevoPoli = polideportivoRepository.findById(dto.getIdPolideportivo())
                    .orElseThrow(() -> new PolideportivoNotFoundException(dto.getIdPolideportivo()));
            existente.setPolideportivo(nuevoPoli);
        }
        if (!existente.getTipoPista().getIdTipoPista().equals(dto.getIdTipoPista())) {
            TipoPista nuevoTipo = tipoPistaRepository.findById(dto.getIdTipoPista())
                    .orElseThrow(() -> new BadRequestException(
                            "No existe el tipo de pista con id " + dto.getIdTipoPista()));
            existente.setTipoPista(nuevoTipo);
        }

        // Duplicado por nombre EXCLUYENDO el propio id
        boolean duplicada = pistaRepository
                .existsByNombrePistaAndPolideportivo_IdPolideportivoAndIdPistaNot(
                        dto.getNombrePista(), dto.getIdPolideportivo(), id);
        if (duplicada) {
            throw new BadRequestException("Ya existe otra pista con el nombre '"
                    + dto.getNombrePista() + "' en este polideportivo.");
        }

        // El mapper actualiza los campos primitivos (nombre, capacidad, precio,
        // requierePagoPrevio, tiempoMinCancelacionHoras, activa).
        pistaMapper.updateEntityFromDto(dto, existente);

        return pistaMapper.toResponse(pistaRepository.save(existente));
    }

    // ─── Cambiar estado (mantenimiento) ──────────────────────────────────────

    /**
     * Activa/desactiva una pista. Mantengo este método separado porque es la
     * acción típica del admin (PATCH /api/pistas/{id}/estado) y no requiere
     * pasar el resto del DTO.
     */
    public PistaResponseDTO cambiarEstadoPista(Long id, boolean activa) {
        Pista pista = buscarEntidadPorId(id);
        pista.setActiva(activa);
        return pistaMapper.toResponse(pistaRepository.save(pista));
    }

    // ─── Eliminar ────────────────────────────────────────────────────────────

    public void eliminar(Long id) {
        if (!pistaRepository.existsById(id)) {
            throw new PistaNotFoundException(id);
        }
        pistaRepository.deleteById(id);
    }
}
