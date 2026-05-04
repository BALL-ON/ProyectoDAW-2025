package com.ballon.backend.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballon.backend.dtos.PolideportivoRequestDTO;
import com.ballon.backend.dtos.PolideportivoResponseDTO;
import com.ballon.backend.exception.ConflictException;
import com.ballon.backend.exception.PolideportivoDuplicatedException;
import com.ballon.backend.exception.PolideportivoNotFoundException;
import com.ballon.backend.mapper.PolideportivoMapper;
import com.ballon.backend.models.Polideportivo;
import com.ballon.backend.repositories.PistaRepository;
import com.ballon.backend.repositories.PolideportivoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PolideportivoService {

    private final PolideportivoRepository polideportivoRepository;
    private final PistaRepository pistaRepository;
    private final PolideportivoMapper polideportivoMapper;

    // ─── Lecturas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PolideportivoResponseDTO> listarTodos() {
        return polideportivoMapper.toResponseList(polideportivoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public PolideportivoResponseDTO buscarPorId(Long id) {
        return polideportivoMapper.toResponse(buscarEntidadPorId(id));
    }

    /** Lookup interno (devuelve la entidad). Lo usamos en update y delete. */
    private Polideportivo buscarEntidadPorId(Long id) {
        return polideportivoRepository.findById(id)
                .orElseThrow(() -> new PolideportivoNotFoundException(id));
    }

    // ─── Crear ───────────────────────────────────────────────────────────────

    public PolideportivoResponseDTO crear(PolideportivoRequestDTO dto) {
        if (polideportivoRepository.existsByNombre(dto.getNombre())) {
            // El antiguo constructor de la excepción recibía la entidad;
            // como aún no la hemos persistido, la creamos en memoria sólo
            // para reutilizar el mensaje de error.
            Polideportivo aux = new Polideportivo();
            aux.setNombre(dto.getNombre());
            throw new PolideportivoDuplicatedException(aux);
        }
        Polideportivo nuevo = polideportivoMapper.toEntity(dto);
        return polideportivoMapper.toResponse(polideportivoRepository.save(nuevo));
    }

    // ─── Actualizar ──────────────────────────────────────────────────────────

    public PolideportivoResponseDTO actualizar(Long id, PolideportivoRequestDTO dto) {
        Polideportivo existente = buscarEntidadPorId(id);

        // Comprobamos duplicados pero EXCLUYENDO el propio id, para permitir
        // editar otros campos (dirección, población...) sin cambiar el nombre.
        if (polideportivoRepository.existsByNombreAndIdPolideportivoNot(dto.getNombre(), id)) {
            Polideportivo aux = new Polideportivo();
            aux.setNombre(dto.getNombre());
            throw new PolideportivoDuplicatedException(aux);
        }

        polideportivoMapper.updateEntityFromDto(dto, existente);
        return polideportivoMapper.toResponse(polideportivoRepository.save(existente));
    }

    // ─── Eliminar ────────────────────────────────────────────────────────────

    /**
     * Elimina un polideportivo. Si tiene pistas asociadas, lanza ConflictException.
     */
    public void eliminar(Long id) {
        if (!polideportivoRepository.existsById(id)) {
            throw new PolideportivoNotFoundException(id);
        }
        boolean tienePistas = !pistaRepository.findByPolideportivoIdPolideportivo(id).isEmpty();
        if (tienePistas) {
            throw new ConflictException("No se puede eliminar: este centro tiene pistas asociadas.");
        }
        polideportivoRepository.deleteById(id);
    }
}
