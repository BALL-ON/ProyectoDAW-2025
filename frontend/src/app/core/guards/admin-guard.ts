import { CanActivateFn, Router } from '@angular/router';
import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export const adminGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const platformId = inject(PLATFORM_ID);

  // preguntamos si estamos en el navegador
  if (isPlatformBrowser(platformId)) {

    const rol = sessionStorage.getItem('user_rol');

    if (rol === 'ROLE_Admin_Centro' || rol === 'ROLE_Admin_Global') {
      return true;
    } else {
      console.warn('Acceso denegado: No eres administrador.');
      router.navigate(['/login']);
      return false; 
    }
  }

  // Devolvemos true para que no aborte la carga al recargar la pagina.
  // En cuanto la página llegue al navegador, se ejecuta el bloque de arriba y hace la seguridad real.
  return true; 
};
