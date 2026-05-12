import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdminCentroService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:9999/api';

  validarReservaQr(codigoQr: string): Observable<any> {
    // Usamos POST porque estamos cambiando el estado de la reserva a "USADA"
    return this.http.post<any>(`${this.apiUrl}/admin-centro/validar-qr/${codigoQr}`, {});
  }
  
}
