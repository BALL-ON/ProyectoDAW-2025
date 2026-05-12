import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MensajeContacto } from '../../services/mensaje-contacto';
import { AuthService } from '../../services/auth';
import { Usuario } from '../../services/usuario';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './contacto.html',
  styleUrl: './contacto.css',
})
export class Contacto {
  private contactoServicio = inject(MensajeContacto);
  private usuarioServicio = inject(Usuario);
  authService = inject(AuthService);

  enviando = false;
  enviado = false;

  successMessage = '';
  showSuccessMessage = false;

  form: any;

  email = '';
  telefono = '';

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      nombre: '',
      email: '',
      telefono: '',
      asunto: 'reserva',
      mensaje: ['', [Validators.required, Validators.minLength(10)]],
      fecha_envio: this.obtenerFechaActual(),
      leido: false,
    });
  }

  enviarMensaje() {
    this.enviando = true;

    this.usuarioServicio
      .obtenerMiPerfil()
      .pipe(
        switchMap((perfil) => {
          this.form.patchValue({ nombre: perfil.nombre });
          this.form.patchValue({ email: perfil.email });
          this.form.patchValue({ telefono: perfil.telefono });
          console.log('Perfil obtenido:', perfil);
          console.log('email: ', this.form.value.email);
          console.log('telefono: ', this.form.value.telefono);

          return this.contactoServicio.enviar(this.form.value);
        }),
      )
      .subscribe({
        next: () => {
          this.enviando = false;
          this.enviado = true;
          this.form.reset();
        },
        error: (err) => {
          console.error('Error al enviar el mensaje:', err);
          this.enviando = false;
        },
      });

    this.successMessage = '✓ Mensaje enviado con éxito. Nos pondremos en contacto contigo pronto.';
    this.showSuccessMessage = true;

    setTimeout(() => {
      this.showSuccessMessage = false;
    }, 10000);
  }

  private obtenerFechaActual(): string {
    const fecha = new Date();
    const año = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${año}-${mes}-${dia}`;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
