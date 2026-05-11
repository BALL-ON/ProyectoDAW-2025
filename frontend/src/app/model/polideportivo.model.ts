// Espejo del enum del backend (com.ballon.backend.models.enums.MetodoPago)
export type MetodoPago = 'Gratis' | 'Presencial' | 'Online';

/**
 * Espejo de PolideportivoResponseDTO.java
 */
export interface PolideportivoResponse {
  idPolideportivo: number;
  nombre: string;
  direccion: string;
  poblacion: string;
  metodoPagoPreferido: MetodoPago;
}

/**
 * Espejo de PolideportivoRequestDTO.java
 * Lo usaremos cuando hagamos el panel de admin.
 */
export interface PolideportivoRequest {
  nombre: string;
  direccion: string;
  poblacion: string;
  metodoPagoPreferido?: MetodoPago;
}
