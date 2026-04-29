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
  
  // La URL de tu backend
  private apiUrl = 'http://localhost:8080/auth';

  // Las key para guardar las cosas en el navegador
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROL_KEY = 'user_rol';

  // Método que llamamos desde el componente Login
  login(email: string, contrasena: string, remember: boolean): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, contrasena, remember }).pipe(
      tap(response => {
        // Cuando Spring responde, guardamos el token y el rol
        if (response.token) {
          this.guardarSesion(response.token, response.rol, remember);
        }
      })
    );
  }

  private guardarSesion(token: string, rol: string, remember: boolean): void {
    if (remember) {
      localStorage.setItem(this.TOKEN_KEY, token);
      localStorage.setItem(this.ROL_KEY, rol);
    } else {
      sessionStorage.setItem(this.TOKEN_KEY, token);
      sessionStorage.setItem(this.ROL_KEY, rol);
    }
  }

  // Método para cerrar sesión
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROL_KEY);
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.ROL_KEY);
  }
}
