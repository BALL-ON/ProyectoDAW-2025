import { Component, signal, computed, inject } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {
  
  private authService = inject(AuthService);

  // ESTADOS: Ahora solo tenemos paso 1 (Formulario) y paso 2 (Éxito)
  protected step = signal<1 | 2>(1);
  protected showPassword = signal(false);
  protected showConfirm = signal(false);
  protected termsChecked = signal(false);
  protected passwordValue = signal('');
  protected nombreUsuario = signal('');

  // FORMULARIO SIMPLIFICADO
  registroForm = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(2)]),
    apellidos: new FormControl('', [Validators.required, Validators.minLength(2)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    telefono: new FormControl('', [Validators.pattern('^[0-9]{9,15}$')]),
    contrasena: new FormControl('', [
      Validators.required, 
      Validators.minLength(8),
      Validators.pattern(/(?=.*[A-Z])(?=.*\d)/)
    ]),
    confirmar: new FormControl('', [Validators.required]),
  }, { validators: this.matchPasswords });

  constructor() {
    this.registroForm.get('contrasena')?.valueChanges.subscribe(val => {
      this.passwordValue.set(val || '');
    });
  }

  matchPasswords(group: AbstractControl): ValidationErrors | null {
    const pass = group.get('contrasena')?.value;
    const confirm = group.get('confirmar')?.value;
    return pass === confirm ? null : { passwordsMismatch: true };
  }

  protected pwScore = computed(() => {
    const pw = this.passwordValue();
    let score = 0;
    if (pw.length >= 8) score++;
    if (/[A-Z]/.test(pw)) score++;
    if (/[0-9]/.test(pw)) score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;
    return score; 
  });

  protected pwLabel = computed(() => {
    const score = this.pwScore();
    if (this.passwordValue().length === 0) return { text: 'Mínimo 8 caracteres', color: 'var(--text-muted)' };
    const labels = ['Muy débil', 'Débil', 'Media', 'Fuerte'];
    const colors = ['#ff4d4d', '#ff4d4d', '#ffaa00', '#22d87a'];
    return { text: labels[score - 1] || 'Mínimo 8 caracteres', color: colors[score - 1] || 'var(--text-muted)' };
  });

  togglePass(field: 'pass' | 'confirm') {
    if (field === 'pass') this.showPassword.update(v => !v);
    else this.showConfirm.update(v => !v);
  }

  toggleTerms() {
    this.termsChecked.update(v => !v);
  }

  onSubmit() {
    // Validamos formulario Y que los términos estén marcados
    if (this.registroForm.valid && this.termsChecked()) {
      const formDatos = this.registroForm.value;
      
      const datosFinales = {
        nombre: formDatos.nombre,
        apellidos: formDatos.apellidos,
        email: formDatos.email,
        contrasena: formDatos.contrasena,
        telefono: formDatos.telefono
      };

      console.log('Enviando a backend:', datosFinales);
      
      this.authService.registro(datosFinales).subscribe({
        next: (respuestaBackend) => {
          console.log('Respuesta del servidor:', respuestaBackend);
          this.nombreUsuario.set(formDatos.nombre ?? '');
          this.step.set(2);
        },
        error: (error) => {
          console.error('Error al registrar:', error);
          alert('Hubo un error al crear la cuenta.');
        }
      });

    } else {
      this.registroForm.markAllAsTouched();
      if (!this.termsChecked()) {
        alert('Debes aceptar los términos y condiciones.');
      }
    }
  }
}