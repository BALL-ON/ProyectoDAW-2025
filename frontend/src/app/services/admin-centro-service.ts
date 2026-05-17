import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { DiaSemana } from '../model/horario.model';

/** DTO que espera el backend para crear un horario */
export interface HorarioRequest {
  idPista: number;
  diaSemana: DiaSemana;
  horaInicio: string;  // "HH:mm:ss"
  horaFin: string;     // "HH:mm:ss"
}

/**
 * Configuración de un día de la semana para el formulario
 * de creación de pista.
 */
export interface DiaHorario {
  diaSemana: DiaSemana;
  activo: boolean;
  horaInicio: string;
  horaFin: string;
}

/** Días con su horario por defecto (L-V 08-22, S 09-20, D 09-14) */
export const HORARIO_DEFAULT: DiaHorario[] = [
  { diaSemana: 'Lunes',     activo: true,  horaInicio: '08:00:00', horaFin: '22:00:00' },
  { diaSemana: 'Martes',    activo: true,  horaInicio: '08:00:00', horaFin: '22:00:00' },
  { diaSemana: 'Miercoles', activo: true,  horaInicio: '08:00:00', horaFin: '22:00:00' },
  { diaSemana: 'Jueves',    activo: true,  horaInicio: '08:00:00', horaFin: '22:00:00' },
  { diaSemana: 'Viernes',   activo: true,  horaInicio: '08:00:00', horaFin: '22:00:00' },
  { diaSemana: 'Sabado',    activo: true,  horaInicio: '09:00:00', horaFin: '20:00:00' },
  { diaSemana: 'Domingo',   activo: false, horaInicio: '09:00:00', horaFin: '14:00:00' },
];

@Injectable({
  providedIn: 'root',
})
export class AdminCentroService {

  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/api`;

  validarReservaQr(codigoQr: string): Observable<any> {
    // Usamos POST porque estamos cambiando el estado de la reserva a "USADA"
    return this.http.post<any>(`${this.apiUrl}/admin-centro/validar-qr/${codigoQr}`, {});
  }

  // ── Horarios ────────────────────────────────────────────────────────────

  /**
   * Crea un único horario para una pista.
   * Endpoint: POST /api/horarios
   */
  crearHorario(horario: HorarioRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/horarios`, horario);
  }

  /**
   * Crea todos los horarios activos de una pista en paralelo.
   * Filtra automáticamente los días marcados como inactivos.
 
   */
  crearHorariosParaPista(idPista: number, dias: DiaHorario[]): Observable<any[]> {
    const requests = dias
      .filter(d => d.activo)
      .map(d =>
        this.crearHorario({
          idPista,
          diaSemana: d.diaSemana,
          horaInicio: d.horaInicio,
          horaFin:    d.horaFin,
        })
      );

    // forkJoin lanza todas las peticiones en paralelo y emite cuando
    // todas han completado. Si alguna falla, el observable emite error.
    return forkJoin(requests);
  }
}