package com.ballon.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Pista;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {

    /** Todas las pistas (activas o no) de un polideportivo. */
    List<Pista> findByPolideportivoIdPolideportivo(Long polideportivoId);

    /** SOLO las pistas ACTIVAS de un polideportivo. Filtrado en SQL, no en memoria. */
    List<Pista> findByPolideportivoIdPolideportivoAndActivaTrue(Long polideportivoId);

    /** Lookup de duplicados al CREAR. */
    Optional<Pista> findByNombrePistaAndPolideportivo_IdPolideportivo(
            String nombre, Long idPolideportivo);

    /**
     * Lookup de duplicados al EDITAR excluyendo el propio id.
     * Si existe el resultado, hay otra pista con ese nombre en el mismo poli.
     */
    boolean existsByNombrePistaAndPolideportivo_IdPolideportivoAndIdPistaNot(
            String nombre, Long idPolideportivo, Long idPista);
}
