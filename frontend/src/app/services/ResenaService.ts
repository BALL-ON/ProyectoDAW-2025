import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResenaService {
  private apiUrl = 'http://localhost:9999/api/resenas'; 

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = sessionStorage.getItem('token');
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  // Descarga TODAS las reseñas del polideportivo
  obtenerPorPolideportivo(idPolideportivo: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/polideportivo/${idPolideportivo}`, { headers: this.getHeaders() });
  }

  // Ocultar o volver a mostrar una reseña 
  cambiarVisibilidad(idResena: number, visible: boolean): Observable<any> {
    const params = new HttpParams().set('visible', visible);
    return this.http.patch<any>(`${this.apiUrl}/${idResena}/visibilidad`, null, { headers: this.getHeaders(), params });
  }
}
