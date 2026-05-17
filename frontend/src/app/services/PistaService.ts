import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PistaRequest, PistaResponse } from '../model/pista.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PistaService {
  private readonly apiUrl = `${environment.apiUrl}/api/pistas`;

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
 
  /** GET /api/pistas/polideportivo/{idPolideportivo}/todas */
  obtenerTodasPistasPorPolideportivo(idPolideportivo: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/polideportivo/${idPolideportivo}/todas`);
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
  cambiarEstado(id: number, activa: boolean): Observable<any> {
    const params = new HttpParams().set('activa', activa);
    
    // Sacamos el token del sessionStorage y creamos la cabecera
    const token = sessionStorage.getItem('token');
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    // Añadimos los headers a la petición
    return this.http.patch<any>(`${this.apiUrl}/${id}/estado`, null, { headers, params });
  }

  /** DELETE /api/pistas/{id} (ADMIN) */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
