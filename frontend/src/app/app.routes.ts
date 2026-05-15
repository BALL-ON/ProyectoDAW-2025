import { Routes } from '@angular/router';
import { Inicio } from './components/inicio/inicio';
import { Faq } from './components/faq/faq';
import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';
import { Polideportivos } from './components/polideportivos/polideportivos';
import { Contacto } from './components/contacto/contacto';
import { Reserva } from './components/reserva/reserva';
import { Pistas } from './components/pistas/pistas';
import { Perfil } from './components/perfil/perfil';
import { MisReservas } from './components/mis-reservas/mis-reservas';
import { DashboardAdmin } from './components/admin/dashboard-admin/dashboard-admin';
import { adminGuard } from './core/guards/admin-guard';
import { EscanerQr } from './components/admin-centro/escaner-qr/escaner-qr';
import { TerminosServicio } from './components/shared/enlaces-footer/terminos-servicio/terminos-servicio';
import { PoliticaPrivacidad } from './components/shared/enlaces-footer/politica-privacidad/politica-privacidad';
import { Cookies } from './components/shared/enlaces-footer/cookies/cookies';
import { Pago } from './components/pago/pago';


export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'faq', component: Faq },
  { path: 'polideportivos', component: Polideportivos },
  { path: 'polideportivos/:id/pistas', component: Pistas },
  { path: 'reserva/:idPista', component: Reserva },
  { path: 'contacto', component: Contacto },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'perfil', component: Perfil},
  { path: 'mis-reservas', component: MisReservas },
  { path: 'terminos-servicio', component: TerminosServicio },
  { path: 'politica-privacidad', component: PoliticaPrivacidad },
  { path: 'cookies', component: Cookies },
  { path: 'admin/dashboard', component: DashboardAdmin, canActivate: [adminGuard] },
  { path: 'admin/escanear-qr', component: EscanerQr },
  { path: 'pago/:idReserva', component: Pago }
];
