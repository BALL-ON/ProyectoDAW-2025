import { AuthService } from './auth';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { MensajeDTO } from '../interfaces/mensaje-dto';

/**
 * Servicio para el formulario de contacto y la gestión de mensajes recibidos.
 *
 * - `enviar` es PÚBLICO: lo usa el formulario de contacto sin necesidad de
 *   sesión iniciada.
 * - El resto son endpoints de administración (Admin_Global / Admin_Centro)
 *   que requieren token. El `authInterceptor` global ya adjunta el header
 *   `Authorization: Bearer <token>` en cada petición, así que aquí no
 *   hace falta tocar nada.
 */
@Injectable({ providedIn: 'root' })
export class MensajeContacto {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:9999/api/contacto';
  private authService = inject(AuthService);

  /**
   * POST /api/contacto — Público.
   * Envía un mensaje desde el formulario de contacto.
   */
  enviar(mensaje: MensajeDTO): Observable<MensajeDTO> {

    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);

    return this.http.post<MensajeDTO>(this.apiUrl, mensaje, { headers });
  }

  /**
   * GET /api/contacto — Admin.
   * Devuelve todos los mensajes recibidos (leídos y pendientes).
   */
  listarTodos(): Observable<MensajeDTO[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<MensajeDTO[]>(this.apiUrl, { headers });
  }

  /**
   * GET /api/contacto/pendientes — Admin.
   * Devuelve sólo los mensajes que aún no han sido marcados como leídos.
   */
  listarPendientes(): Observable<MensajeDTO[]> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<MensajeDTO[]>(`${this.apiUrl}/pendientes`, { headers });
  }

  /**
   * PATCH /api/contacto/{id}/leido — Admin.
   * Marca un mensaje como leído. El backend no devuelve cuerpo.
   */
  marcarLeido(idMensaje: number): Observable<void> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.patch<void>(`${this.apiUrl}/${idMensaje}/leido`, {}, { headers });
  }

  /**
   * DELETE /api/contacto/{id} — Admin.
   * Elimina permanentemente un mensaje.
   */
  eliminar(idMensaje: number): Observable<void> {
    const token = this.authService.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/${idMensaje}`, { headers });
  }
}
