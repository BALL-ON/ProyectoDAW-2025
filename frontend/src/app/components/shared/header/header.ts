import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
activeRoute = signal<string>('inicio');
  menuOpen = signal<boolean>(false);
 
  setActive(route: string): void {
    this.activeRoute.set(route);
    this.menuOpen.set(false); // Cierra el menú móvil al navegar
  }
 
  toggleMenu(): void {
    this.menuOpen.set(!this.menuOpen());
  }
}
