import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'polideportivos',           
    renderMode: RenderMode.Server
  },
  {
    path: 'reserva/:idPista',
    renderMode: RenderMode.Server
  },
  {
    path: 'polideportivos/:id/pistas',
    renderMode: RenderMode.Server
  },
  {
    path: 'pago/:idReserva',
    renderMode: RenderMode.Server 
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
