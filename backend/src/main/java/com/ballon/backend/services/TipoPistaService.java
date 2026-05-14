package com.ballon.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballon.backend.dtos.TipoPistaDTO;
import com.ballon.backend.exception.TipoPistaDuplicatedException;
import com.ballon.backend.mapper.TipoPistaMapper;
import com.ballon.backend.models.TipoPista;
import com.ballon.backend.repositories.TipoPistaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoPistaService {

    private final TipoPistaRepository tipoPistaRepository;
    private final TipoPistaMapper tipoPistaMapper;

    /** Listado de tipos de pista. Lectura, sin escritura. */
    @Transactional(readOnly = true)
    public List<TipoPistaDTO> listarTipos() {
        return tipoPistaRepository.findAll().stream()
                .map(tipoPistaMapper::toTipoPistaDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un tipo de pista. Verifica que no exista otro con el mismo
     * nombreTipo antes de persistir.
     */
    public TipoPistaDTO crearTipo(TipoPistaDTO dto) {
        if (tipoPistaRepository.existsByNombreTipo(dto.getNombreTipo())) {
            throw new TipoPistaDuplicatedException(tipoPistaMapper.toTipoPistaEntity(dto));
        }

        TipoPista entidad = tipoPistaMapper.toTipoPistaEntity(dto);
        TipoPista guardada = tipoPistaRepository.save(entidad);
        return tipoPistaMapper.toTipoPistaDto(guardada);
    }
}