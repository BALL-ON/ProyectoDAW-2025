import { Component, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink, Router, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css',
})

export class Header {

  authService = inject(AuthService);
  private router = inject(Router);

  menuOpen = signal<boolean>(false);
  dropdownOpen = signal<boolean>(false);

  rolUsuario: string | null = null;

  private platformId = inject(PLATFORM_ID);

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.rolUsuario = sessionStorage.getItem('user_rol');
    }
  }

  toggleMenu(): void {
    this.menuOpen.set(!this.menuOpen());
  }

  toggleDropdown(): void {
    this.dropdownOpen.set(!this.dropdownOpen());
  }

  closeMobileMenu(): void {
    this.menuOpen.set(false);
  }

  closeDropdown(): void {
    this.dropdownOpen.set(false);
  }

  logout(): void {
    this.authService.logout();
    this.closeDropdown();
    this.router.navigate(['/login']); // Redirige al usuario tras salir
  }
}
