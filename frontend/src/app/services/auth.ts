import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  
  // URL principal del backend
  private apiUrl = 'http://localhost:9999/auth';

  // Las llaves para guardar en el navegador
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROL_KEY = 'user_rol';


  // Iniciar Sesión
  login(email: string, contrasena: string, remember: boolean): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, contrasena, remember }).pipe(
      tap(response => {
        if (response.token) {
          this.guardarSesion(response.token, response.rol, remember);
        }
      })
    );
  }

  // Registrar Usuario
  registro(datosRegistro: any): Observable<any> {
    // Hace un POST al endpoint /auth/register
    return this.http.post(`${this.apiUrl}/register`, datosRegistro);
  }


  // Guarda el token y el rol
  private guardarSesion(token: string, rol: string, remember: boolean): void {
    if (remember) {
      localStorage.setItem(this.TOKEN_KEY, token);
      localStorage.setItem(this.ROL_KEY, rol);
    } else {
      sessionStorage.setItem(this.TOKEN_KEY, token);
      sessionStorage.setItem(this.ROL_KEY, rol);
    }
  }

  // Recupera el token para enviarlo en futuras peticiones
  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY) || localStorage.getItem(this.TOKEN_KEY);
  }

  // Recupera el rol para saber si es Admin o Usuario
  getRol(): string | null {
    return sessionStorage.getItem(this.ROL_KEY) || localStorage.getItem(this.ROL_KEY);
  }

  // Comprueba si el usuario está logueado
  isLoggedIn(): boolean {
    // Devuelve true si hay un token guardado y false si el token es null
    return !!this.getToken(); 
  }

  // Borra todo al cerrar sesión
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROL_KEY);
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.ROL_KEY);
  }
}