import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  PolideportivoRequest,
  PolideportivoResponse,
} from '../model/polideportivo.model';

@Injectable({
  providedIn: 'root',
})
export class PolideportivoService {
  // Ajusta el host/puerto si tu Spring Boot corre en otro sitio.
  private readonly apiUrl = 'http://localhost:8080/api/polideportivos';

  constructor(private http: HttpClient) {}

  /** GET /api/polideportivos */
  listarTodos(): Observable<PolideportivoResponse[]> {
    return this.http.get<PolideportivoResponse[]>(this.apiUrl);
  }

  /** GET /api/polideportivos/{id} */
  obtenerPorId(id: number): Observable<PolideportivoResponse> {
    return this.http.get<PolideportivoResponse>(`${this.apiUrl}/${id}`);
  }

  /** POST /api/polideportivos (ADMIN) */
  crear(dto: PolideportivoRequest): Observable<PolideportivoResponse> {
    return this.http.post<PolideportivoResponse>(this.apiUrl, dto);
  }

  /** PUT /api/polideportivos/{id} (ADMIN) */
  actualizar(id: number, dto: PolideportivoRequest): Observable<PolideportivoResponse> {
    return this.http.put<PolideportivoResponse>(`${this.apiUrl}/${id}`, dto);
  }

  /** DELETE /api/polideportivos/{id} (ADMIN) */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
