import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  // Signal para controlar si se ve la contraseña
  protected showPassword = signal(false);

  // Inyeccion del servicio y el enrutador
  private authService = inject(AuthService);
  private router = inject(Router);

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
      // Se usa el operador ?? para decirle: "Si es null o undefined, usa '' o false"
      const email = this.loginForm.value.email ?? '';
      const contrasena = this.loginForm.value.contrasena ?? '';
      const remember = this.loginForm.value.remember ?? false;

      this.authService.login(email, contrasena, remember).subscribe({
        next: (respuesta) => {
          //console.log('¡Login correcto! Token recibido:', respuesta.token);
          
          // Redirigimos al usuario a la página principal
          this.router.navigate(['/']); 
        },
        error: (err) => {
          console.error('Error en el login', err);
          alert('El usuario o la contraseña no son correctos.');
        }
      });
    }
  }
}
