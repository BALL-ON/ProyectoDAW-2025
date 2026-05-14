package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para el endpoint GET /api/reservas/{id}/qr.
 *
 * imagenBase64 viene con el prefijo "data:image/png;base64,..." para
 * meterlo directamente en el src de un img en el frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrReservaDTO {
    private String imagenBase64;
    private String tokenQr;
}