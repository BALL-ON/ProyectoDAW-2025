package com.ballon.backend.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    private Long idUsuario; //esto se saca del Token de sesión

    @NotNull
    private Long idPista;

    @NotNull
    @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
    private LocalDate fechaReserva;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin; 
    // Alternativamente, puedes pedir 'duracionEnMinutos' y calcular la horaFin en backend.
}
