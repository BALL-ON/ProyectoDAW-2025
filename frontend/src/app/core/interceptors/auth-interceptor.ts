import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Inyectamos el Router para poder hacer la redirección
  const router = inject(Router);

  // Dejamos que la petición viaje hacia Spring Boot
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      
      // 401 = No autorizado (token caducado o sin token)
      // 403 = Prohibido (el token es válido pero no tienes permisos para esa ruta)
      if (error.status === 401 || error.status === 403) {
        console.warn('Sesión expirada o inválida. Cerrando sesión...');
        // Limpiamos el rastro del token en el navegador
        localStorage.removeItem('token');

        router.navigate(['/login'], { queryParams: { expirada: 'true' } });  // Le pasamos un parametro oculto en la url al login para mostrar mensaje
      }

      return throwError(() => error);
    })
  );
};