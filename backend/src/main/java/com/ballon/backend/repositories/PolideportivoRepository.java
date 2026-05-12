package com.ballon.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Query("SELECT p FROM Polideportivo p WHERE " +
            "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
            "(:poblacion IS NULL OR LOWER(p.poblacion) LIKE LOWER(CONCAT('%', :poblacion, '%')))")
     Page<Polideportivo> buscarPolideportivosPaginadosYFiltrados(
         @Param("nombre") String nombre, 
         @Param("poblacion") String poblacion, 
         Pageable pageable
     );
}
