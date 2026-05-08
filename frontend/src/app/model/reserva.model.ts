// enum del backend (com.ballon.backend.models.enums.EstadoReserva)
export type EstadoReserva = 'Confirmada' | 'Cancelada' | 'Disfrutada' | 'No_Asistido';

// enum del backend (com.ballon.backend.models.enums.EstadoPago)
export type EstadoPago = 'Pendiente' | 'Pagado' | 'Reembolsado';

/**
 * ReservaRequestDTO.java
 * Lo que mandamos al backend al crear una reserva.
 * idUsuario es opcional porque normalmente lo saca el backend del token de sesión.
 */
export interface ReservaRequest {
  idUsuario?: number;
  idPista: number;
  fechaReserva: string; // ISO yyyy-MM-dd
  horaInicio: string;   // HH:mm o HH:mm:ss
  horaFin: string;      // HH:mm o HH:mm:ss
}

/**
 * ReservaResponseDTO.java
 * Lo que el backend devuelve al consultar reservas.
 */
export interface ReservaResponse {
  idReserva: number;

  idUsuario: number;
  nombreUsuarioCompleto?: string;

  idPista: number;
  nombrePista: string;
  nombrePolideportivo: string;

  fechaReserva: string; // yyyy-MM-dd
  horaInicio: string;   // HH:mm:ss
  horaFin: string;      // HH:mm:ss
  precioTotal: number;

  estadoReserva: EstadoReserva;
  estadoPago: EstadoPago;
  tokenQr?: string;
  tieneResena: boolean;
}

/**
 * OcupacionSlotDTO.java
 * Sólo rangos ocupados, sin info personal.
 */
export interface OcupacionSlot {
  horaInicio: string; // HH:mm:ss
  horaFin: string;    // HH:mm:ss
}

/**
 * Modelo interno del componente para representar un slot horario clicable.
 * NO va al backend, se usa sólo para pintar el grid.
 */
export interface SlotHorario {
  id: number;     // hora (8, 9, 10...) — útil para ordenar y comparar
  label: string;  // "08:00 - 09:00"
  start: string;  // "08:00"
  end: string;    // "09:00"
}

/**
 * Modelo para reseñas
 */
export interface ResenaRequest {
  idReserva: number;
  puntuacion: number;
  comentario: string;
}
