package com.ballon.backend.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ballon.backend.models.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // ─── Consultas derivadas (Spring Data las genera por el nombre) ─────────
 
    List<Reserva> findByUsuarioIdUsuario(Long usuarioId);
 
    /** Útil para el panel de admin. */
    List<Reserva> findByPistaIdPista(Long pistaId);
 
    /** Reservas de un usuario filtradas por pista (usado en /mis-reservas/pista/{id}). */
    List<Reserva> findByUsuarioIdUsuarioAndPistaIdPista(Long idUsuario, Long idPista);
 
    /**
     * Un usuario sólo puede
     * dejar una reseña si ha reservado la pista al menos una vez.
     */
    boolean existsByUsuarioIdUsuarioAndPistaIdPista(Long idUsuario, Long idPista);
 
    // ─── Consultas con @Query (lógica más fina) ─────────────────────────────
 
    /**
     * Reservas CONFIRMADAS de una pista en una fecha concreta.
     * Se usa para construir la lista de slots ocupados que devolvemos al
     * frontend a través de OcupacionSlotDTO.
     */
    @Query("SELECT r FROM Reserva r "
         + "WHERE r.pista.idPista = :idPista "
         + "  AND r.fechaReserva = :fecha "
         + "  AND r.estadoReserva = com.ballon.backend.models.enums.EstadoReserva.Confirmada")
    List<Reserva> findConfirmadasByPistaAndFecha(@Param("idPista") Long idPista,
                                                 @Param("fecha") LocalDate fecha);
 
    /**
     * Detecta CUALQUIER solapamiento contra reservas Confirmadas en la misma
     * pista y día.
     *
     * Regla matemática de solapamiento de rangos [a,b) y [c,d):
     *      colisión  ⇔  a < d  AND  c < b
     *
     * Aquí: nueva.horaInicio < existente.horaFin
     *  AND  existente.horaInicio < nueva.horaFin
     *
     * Si la lista devuelta está vacía → el horario está libre.
     */
    @Query("SELECT r FROM Reserva r "
         + "WHERE r.pista.idPista = :idPista "
         + "  AND r.fechaReserva = :fecha "
         + "  AND r.estadoReserva = com.ballon.backend.models.enums.EstadoReserva.Confirmada "
         + "  AND r.horaInicio < :horaFin "
         + "  AND r.horaFin   > :horaInicio")
    List<Reserva> findSolapamientos(@Param("idPista") Long idPista,
                                    @Param("fecha") LocalDate fecha,
                                    @Param("horaInicio") LocalTime horaInicio,
                                    @Param("horaFin") LocalTime horaFin);
    
    /*Para buscar la reserva cuando el admin escanea el QR*/
    Optional<Reserva> findByTokenQr(String tokenQr);
    
    /* Busca reservas confirmadas de hoy cuya hora de inicio ya pasó hace más de 30 mins   */
    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = 'Confirmada' " +
           "AND r.fechaReserva = :hoy AND r.horaInicio < :horaLimite")
    List<Reserva> findConfirmadasCaducadasHoy(@Param("hoy") LocalDate hoy, @Param("horaLimite") LocalTime horaLimite);
    
    /* Busca reservas confirmadas de días anteriores */
    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva = 'Confirmada' AND r.fechaReserva < :hoy")
    List<Reserva> findConfirmadasDiasAnteriores(@Param("hoy") LocalDate hoy);
}
