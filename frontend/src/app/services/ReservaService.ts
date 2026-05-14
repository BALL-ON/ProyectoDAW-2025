import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  OcupacionSlot,
  ReservaRequest,
  ReservaResponse,
  ResenaRequest,
  PagoRequest,
  QrReserva
} from '../model/reserva.model';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root',
})
export class ReservaService {
  
  private readonly apiUrl = 'http://localhost:9999/api/reservas';

  constructor(private http: HttpClient) {}
  private authService = inject(AuthService);

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  /** GET /api/reservas/mis-reservas */
  misReservas(): Observable<ReservaResponse[]> {
    return this.http.get<ReservaResponse[]>(`${this.apiUrl}/mis-reservas`, { headers: this.getHeaders() });
  }

  /** GET /api/reservas/mis-reservas/pista/{idPista} */
  misReservasEnPista(idPista: number): Observable<ReservaResponse[]> {
    return this.http.get<ReservaResponse[]>(
      `${this.apiUrl}/mis-reservas/pista/${idPista}`
    );
  }

  /**
   * GET /api/reservas/pista/{idPista}/ocupacion?fecha=YYYY-MM-DD
   * Devuelve sólo los rangos ocupados (sin datos personales) para pintar
   * el grid de slots.
   */
  ocupacionDelDia(idPista: number, fecha: string): Observable<OcupacionSlot[]> {
    const params = new HttpParams().set('fecha', fecha);
    return this.http.get<OcupacionSlot[]>(
      `${this.apiUrl}/pista/${idPista}/ocupacion`,
      { params }
    );
  }

  /** POST /api/reservas */
  crearReserva(dto: ReservaRequest): Observable<ReservaResponse> {
    return this.http.post<ReservaResponse>(this.apiUrl, dto);
  }

  /** DELETE /api/reservas/{id} */
  cancelarReserva(idReserva: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idReserva}`, { headers: this.getHeaders() });
  }

  crearResena(dto: ResenaRequest): Observable<any> {
    return this.http.post<any>('http://localhost:9999/api/resenas', dto, { headers: this.getHeaders() });
  }

  obtenerReservasPorPolideportivo(idPolideportivo: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/polideportivo/${idPolideportivo}/reservas`);
  }

  obtenerReservasPaginadas(idPolideportivo: number, page: number, size: number): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<any>(`${this.apiUrl}/polideportivo/${idPolideportivo}/page`, { headers: this.getHeaders(), params });
  }

  obtenerPorId(idReserva: number): Observable<ReservaResponse> {
  return this.http.get<ReservaResponse>(`${this.apiUrl}/${idReserva}`);
  }

  pagar(idReserva: number, dto: PagoRequest): Observable<ReservaResponse> {
    return this.http.post<ReservaResponse>(`${this.apiUrl}/${idReserva}/pagar`, dto);
  }

  obtenerQr(idReserva: number): Observable<QrReserva> {
  return this.http.get<QrReserva>(`${this.apiUrl}/${idReserva}/qr`);
}
}
