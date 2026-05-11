import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PistaRequest, PistaResponse } from '../model/pista.model';

@Injectable({
  providedIn: 'root',
})
export class PistaService {
  private readonly apiUrl = 'http://localhost:9999/api/pistas';

  constructor(private http: HttpClient) {}

  /** GET /api/pistas */
  listarTodas(): Observable<PistaResponse[]> {
    return this.http.get<PistaResponse[]>(this.apiUrl);
  }

  /** GET /api/pistas/polideportivo/{idPolideportivo} — sólo activas */
  listarActivasPorPolideportivo(idPolideportivo: number): Observable<PistaResponse[]> {
    return this.http.get<PistaResponse[]>(
      `${this.apiUrl}/polideportivo/${idPolideportivo}`
    );
  }

  /** GET /api/pistas/{id} */
  obtenerPorId(id: number): Observable<PistaResponse> {
    return this.http.get<PistaResponse>(`${this.apiUrl}/${id}`);
  }

  /** POST /api/pistas (ADMIN) */
  crear(dto: PistaRequest): Observable<PistaResponse> {
    return this.http.post<PistaResponse>(this.apiUrl, dto);
  }

  /** PUT /api/pistas/{id} (ADMIN) */
  actualizar(id: number, dto: PistaRequest): Observable<PistaResponse> {
    return this.http.put<PistaResponse>(`${this.apiUrl}/${id}`, dto);
  }

  /** PATCH /api/pistas/{id}/estado?activa=true (ADMIN, mantenimiento) */
  cambiarEstado(id: number, activa: boolean): Observable<PistaResponse> {
    const params = new HttpParams().set('activa', activa);
    return this.http.patch<PistaResponse>(`${this.apiUrl}/${id}/estado`, null, { params });
  }

  /** DELETE /api/pistas/{id} (ADMIN) */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
