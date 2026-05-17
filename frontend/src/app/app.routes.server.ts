import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'reserva/:idPista',
    renderMode: RenderMode.Server // O RenderMode.Client
  },
  {
    path: 'polideportivos/:id/pistas',
    renderMode: RenderMode.Server // o RenderMode.Client
  },
  {
    path: 'pago/:idReserva',
    renderMode: RenderMode.Server // o RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
