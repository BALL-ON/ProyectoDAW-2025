import { Injectable, PLATFORM_ID, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

export interface AuthResponse {
  token: string;
  rol: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);  
  
  // URL principal del backend
  private apiUrl = 'http://localhost:9999/auth';

  // Las llaves para guardar en el navegador
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROL_KEY = 'user_rol';

  // Señal que al arrancar comprueba si hay token para sabe si está logueado
  loggedSignal = signal<boolean>(this.isLoggedIn());

  // Iniciar Sesión
  login(email: string, contrasena: string, remember: boolean): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, contrasena, remember }).pipe(
      tap(response => {
        if (response.token) {
          this.guardarSesion(response.token, response.rol, remember);
          this.loggedSignal.set(true);
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
    // Comprobamos que estamos en el navegador para evitar errores con Angular SSR ya que sessionStorage no existe en el servidor.
      if (isPlatformBrowser(this.platformId)) {
      if (remember) {
        localStorage.setItem(this.TOKEN_KEY, token);
        localStorage.setItem(this.ROL_KEY, rol);
      } else {
        sessionStorage.setItem(this.TOKEN_KEY, token);
        sessionStorage.setItem(this.ROL_KEY, rol);
      }
    }
  }

  // Recupera el token para enviarlo en futuras peticiones
  getToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return sessionStorage.getItem(this.TOKEN_KEY) || localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }

  // Recupera el rol para saber si es Admin o Usuario
  getRol(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return sessionStorage.getItem(this.ROL_KEY) || localStorage.getItem(this.ROL_KEY);
    }
    return null;
  }

  // Comprueba si el usuario está logueado
  isLoggedIn(): boolean {
    // Devuelve true si hay un token guardado y false si el token es null
    return !!this.getToken(); 
  }

  // Borra todo al cerrar sesión
  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.ROL_KEY);
      sessionStorage.removeItem(this.TOKEN_KEY);
      sessionStorage.removeItem(this.ROL_KEY);
    }
  }
}