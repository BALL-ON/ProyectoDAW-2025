import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';
import { ResenaResponseDTO, UsuarioResponseDTO } from '../components/perfil/perfil';
import { environment } from '../../environments/environment';

export interface UsuarioUpdateDTO {
  nombre: string;
  apellidos: string;
  telefono: string;
}

@Injectable({
  providedIn: 'root'
})
export class Usuario{
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  
  private apiUrlUsuarios = `${environment.apiUrl}/api/usuarios`;
  private apiUrlResenas = `${environment.apiUrl}/api/resenas`;

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  obtenerMiPerfil(): Observable<UsuarioResponseDTO> {
    return this.http.get<UsuarioResponseDTO>(`${this.apiUrlUsuarios}/perfil`, { headers: this.getHeaders() });
  }

  obtenerMisResenas(): Observable<ResenaResponseDTO[]> {
    return this.http.get<ResenaResponseDTO[]>(`${this.apiUrlResenas}/mis-resenas`, { headers: this.getHeaders() });
  }

  obtenerMisResenasPaginadas(page: number, size: number): Observable<any> {
    const headers = this.getHeaders();
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
      
    return this.http.get<any>(`${this.apiUrlResenas}/mis-resenas/page`, { headers, params });
  }

  actualizarPerfil(datos: UsuarioUpdateDTO): Observable<UsuarioResponseDTO> {
    return this.http.put<UsuarioResponseDTO>(`${this.apiUrlUsuarios}/perfil`, datos, { headers: this.getHeaders() });
  }

  cambiarPassword(datos: any): Observable<any> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.put(`${this.apiUrlUsuarios}/cambiar-password`, datos, { headers });
  }
}
