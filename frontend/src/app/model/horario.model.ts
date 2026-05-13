/**
 * Días de la semana tal y como los serializa el backend (enum DiaSemana).
 * Ojo: "Miercoles" y "Sabado" sin tilde, igual que en el enum Java.
 */
export type DiaSemana =
  | 'Lunes'
  | 'Martes'
  | 'Miercoles'
  | 'Jueves'
  | 'Viernes'
  | 'Sabado'
  | 'Domingo';

/**
 * Horario de apertura de una pista para un día de la semana concreto.
 * Cada pista puede tener entre 0 y 7 registros: si no hay registro
 * para un día, la pista no abre ese día.
 */
export interface HorarioPistaResponse {
  idHorario: number;
  idPista: number;
  diaSemana: DiaSemana;
  horaInicio: string; // "HH:mm:ss" (Spring serializa LocalTime así)
  horaFin: string;
}
