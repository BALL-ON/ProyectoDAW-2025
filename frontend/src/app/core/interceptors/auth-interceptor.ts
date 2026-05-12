import { isPlatformBrowser } from '@angular/common';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  let requestClonada = req;

  if (isPlatformBrowser(platformId)) {
    const token = sessionStorage.getItem('token');

    // Si hay token, clonamos la petición y le añadimos la cabecera
    if (token) {
      requestClonada = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
  }

  return next(requestClonada).pipe(
    catchError((error: HttpErrorResponse) => {
    if (isPlatformBrowser(platformId)) { 
      if (error.status === 401 && !error.url?.includes('/login')) {
        console.warn('Sesión expirada o sin token.');
        if (typeof window !== 'undefined') {
          sessionStorage.removeItem('token');
        }
        router.navigate(['/login'], { queryParams: { expirada: 'true' } });
      }
    }

      // Si es 403, el token es valido pero no tiene permisos para esa acción
      if (error.status === 403) {
        console.warn('El usuario no tiene el rol necesario.'); 
      }

      return throwError(() => error);
    })
  );
};