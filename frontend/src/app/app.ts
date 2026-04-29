import { Component, inject, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { Footer } from './components/shared/footer/footer';
import { Header } from './components/shared/header/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');

  public router = inject(Router);

  // Función que nos dice si estamos en una página de Auth para no mostrar cabacera y footer
  isAuthPage(): boolean {
    // Si la URL es /login o /registro, devolverá true. 
    return this.router.url === '/login' || this.router.url === '/registro';
  }
}
