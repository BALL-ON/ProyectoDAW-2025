/**
 * Espejo de PistaResponseDTO.java
 */
export interface PistaResponse {
  idPista: number;
  nombrePista: string;
  capacidad: number;
  precioHora: number;
  requierePagoPrevio: boolean;
  tiempoMinCancelacionHoras: number;
  activa: boolean;

  // Relaciones aplanadas
  idPolideportivo: number;
  nombrePolideportivo: string;
  idTipoPista: number;
  nombreTipoPista: string;
}

/**
 * Espejo de PistaRequestDTO.java
 * Lo usaremos en el panel de admin.
 */
export interface PistaRequest {
  idPolideportivo: number;
  idTipoPista: number;
  nombrePista: string;
  capacidad?: number;
  precioHora?: number;
  requierePagoPrevio?: boolean;
  tiempoMinCancelacionHoras?: number;
  activa?: boolean;
}
