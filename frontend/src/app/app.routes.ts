import { Routes } from '@angular/router';
import { Inicio } from './components/inicio/inicio';
import { Faq } from './components/faq/faq';
import { Login } from './components/login/login';
import { Registro } from './components/registro/registro';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'faq', component: Faq },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro }
];
