import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth';
import { ResenaResponseDTO, UsuarioResponseDTO } from '../components/perfil/perfil';

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
  
  private apiUrlUsuarios = 'http://localhost:9999/api/usuarios';
  private apiUrlResenas = 'http://localhost:9999/api/resenas';

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

  actualizarPerfil(datos: UsuarioUpdateDTO): Observable<UsuarioResponseDTO> {
    return this.http.put<UsuarioResponseDTO>(`${this.apiUrlUsuarios}/perfil`, datos, { headers: this.getHeaders() });
  }
}
