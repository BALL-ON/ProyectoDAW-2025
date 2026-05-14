package com.ballon.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para el endpoint POST /api/reservas/{id}/pagar.
 *
 * El número de tarjeta se acepta con espacios (formato visual del usuario)
 * y se normaliza en el servicio antes de validar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotBlank(message = "El titular es obligatorio")
    private String titular;

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9 ]{13,23}$", message = "Número de tarjeta inválido")
    private String numeroTarjeta;

    @NotNull(message = "El mes de caducidad es obligatorio")
    @Min(value = 1, message = "Mes inválido")
    @Max(value = 12, message = "Mes inválido")
    private Integer mesExp;

    @NotNull(message = "El año de caducidad es obligatorio")
    @Min(value = 2024, message = "Año inválido")
    private Integer anioExp;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV inválido")
    private String cvv;
}