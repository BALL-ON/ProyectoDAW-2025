import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminGlobalService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:9999/api/admin'; 

  registrarDirector(datosAdmin: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/director`, datosAdmin);
  }

  obtenerDirectores(pagina: number, tamano: number, nombre?: string, email?: string): Observable<any> {
    let params = new HttpParams().set('page', pagina.toString()).set('size', tamano.toString());

    if (nombre) params = params.set('nombre', nombre);
    if (email) params = params.set('email', email);

    return this.http.get<any>(`${this.apiUrl}/directores`, { params });
  }

  cambiarEstadoDirector(idUsuario: number, suspender: boolean): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/director/${idUsuario}/estado?suspender=${suspender}`, {});
  }

  crearPolideportivo(datosPolideportivo: any): Observable<any> {
    return this.http.post<any>('http://localhost:9999/api/polideportivos', datosPolideportivo);
  }

  obtenerPolideportivos(pagina: number, tamano: number, nombre?: string, poblacion?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', pagina.toString())
      .set('size', tamano.toString());

    if (nombre) params = params.set('nombre', nombre);
    if (poblacion) params = params.set('poblacion', poblacion);

    return this.http.get<any>(`http://localhost:9999/api/polideportivos/paginados`, { params });
  }

}
