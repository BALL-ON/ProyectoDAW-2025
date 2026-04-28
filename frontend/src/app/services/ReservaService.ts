import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva as IReserva } from '../model/reserva.model';

@Injectable({
  providedIn: 'root',
})

export class ReservaService {
  private apiUrl = 'http://localhost:9999/daw/reservas';

  constructor(private http: HttpClient) {}

 buscarReservas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl); 
  }

  crearReserva(reserva: IReserva): Observable<IReserva> {
    return this.http.post<IReserva>(this.apiUrl, reserva);
  }

  eliminarReserva(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  actualizarReserva(id: number, reserva: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, reserva);
  }
}