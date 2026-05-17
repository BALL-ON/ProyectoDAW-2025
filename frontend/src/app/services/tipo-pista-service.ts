import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { TipoPista } from '../model/tipo.pista.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TipoPistaService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/tipos-pista`;

  /* GET /api/tipos-pista — listado para filtros y selectores */
  listarTodos(): Observable<TipoPista[]> {
    return this.http.get<TipoPista[]>(this.apiUrl);
  }

  /* POST /api/tipos-pista — alta de tipo de deporte (sólo Admin Global) */
  crear(tipo: TipoPista): Observable<TipoPista> {
    return this.http.post<TipoPista>(this.apiUrl, tipo);
  }
}