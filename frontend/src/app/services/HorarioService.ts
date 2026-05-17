import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HorarioPistaResponse } from '../model/horario.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class HorarioService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/api/horarios`;

  /** Devuelve todos los horarios semanales de una pista. */
  listarPorPista(idPista: number): Observable<HorarioPistaResponse[]> {
    return this.http.get<HorarioPistaResponse[]>(`${this.apiUrl}/pista/${idPista}`);
  }
}
