package com.ballon.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ballon.backend.models.Polideportivo;

@Repository
public interface PolideportivoRepository extends JpaRepository<Polideportivo, Long> {

    /** Para validar duplicados al CREAR. */
    boolean existsByNombre(String nombre);

    /**
     * Para validar duplicados al EDITAR sin penalizar al propio registro.
     * Comprueba que no exista OTRO polideportivo (id distinto) con ese nombre.
     */
    boolean existsByNombreAndIdPolideportivoNot(String nombre, Long idPolideportivo);
}
