import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  mensajeError: string = '';

  // Signal para controlar si se ve la contraseña
  protected showPassword = signal(false);

  // Inyeccion del servicio y el enrutador
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    // Comprobamos si la URL trae el parametro de expiracion de sesion
    this.route.queryParams.subscribe(params => {
      if (params['expirada']) {
        this.mensajeError = 'Tu sesión ha caducado. Vuelve a entrar.';
      }
    });
  }

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl('', [Validators.required, Validators.minLength(6), Validators.pattern(/(?=.*[A-Z])(?=.*\d)/)]),
    remember: new FormControl(false)
  });

  togglePass() {
    this.showPassword.update(valorActual => !valorActual);
    console.log('Botón pulsado. Visible:', this.showPassword());
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const email = this.loginForm.value.email ?? '';
      const contrasena = this.loginForm.value.contrasena ?? '';
      const remember = this.loginForm.value.remember ?? false;

      this.authService.login(email, contrasena, remember).subscribe({
        next: (respuesta) => {
          sessionStorage.setItem('token', respuesta.token);
          sessionStorage.setItem('user_rol', respuesta.rol);

          if (respuesta.idPolideportivo) {
            sessionStorage.setItem('idPolideportivo', respuesta.idPolideportivo.toString());
          }
          
          this.authService.loggedSignal.set(true); 

          if (respuesta.rol === 'ROLE_Admin_Centro' || respuesta.rol === 'ROLE_Admin_Global') {
            // Si es admin, redirige a panel de gestion
            this.router.navigate(['/admin/dashboard']);
          } else {
            // Si es un cliente redirige a la pantalla de inicio
            this.router.navigate(['/']);
          }
        },
        error: (err) => {
          if (err.status === 403 && err.error?.mensaje) {
            Swal.fire(err.error.mensaje);
          }  else if (err.status === 401) {
            Swal.fire('El usuario o la contraseña no son correctos.');
          } else {
            Swal.fire('Ocurrió un error inesperado al intentar iniciar sesión.');
          }
        }
      });
    }
  }
}
