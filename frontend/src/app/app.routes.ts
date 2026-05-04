import { Routes } from '@angular/router';
import { Inicio } from './components/inicio/inicio';
import { Faq } from './components/faq/faq';
import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';
import { Polideportivos } from './components/polideportivos/polideportivos';
import { Contacto } from './components/contacto/contacto';


export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'faq', component: Faq },
  { path: 'polideportivos', component: Polideportivos },
  { path: 'contacto', component: Contacto },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro }
];
