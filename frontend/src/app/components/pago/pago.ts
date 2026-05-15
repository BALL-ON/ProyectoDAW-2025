import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ReservaService } from '../../services/ReservaService';
import { PagoRequest, ReservaResponse } from '../../model/reserva.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-pago',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './pago.html',
  styleUrl: './pago.css',
})
export class Pago implements OnInit {
  private readonly service = inject(ReservaService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  idReserva!: number;
  reserva: ReservaResponse | null = null;

  cargando = true;
  procesando = false;
  mensajeError: string | null = null;

  // Año actual y 15 años hacia adelante para el selector
  aniosDisponibles: number[] = [];

  pagoForm = new FormGroup({
    titular: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.minLength(3)] }),
    numeroTarjeta: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[0-9 ]{13,23}$/)],
    }),
    mesExp: new FormControl<number | null>(null, [Validators.required, Validators.min(1), Validators.max(12)]),
    anioExp: new FormControl<number | null>(null, [Validators.required]),
    cvv: new FormControl<string>('', { nonNullable: true, validators: [Validators.required, Validators.pattern(/^[0-9]{3,4}$/)] }),
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('idReserva');
    if (!idParam) {
      this.router.navigate(['/mis-reservas']);
      return;
    }
    this.idReserva = Number(idParam);

    const anioActual = new Date().getFullYear();
    for (let i = 0; i < 15; i++) this.aniosDisponibles.push(anioActual + i);

    this.cargarReserva();

    // Máscara visual: agrupa el número en bloques de 4
    this.pagoForm.get('numeroTarjeta')!.valueChanges.subscribe((valor) => {
      if (!valor) return;
      const limpio = valor.replace(/\s+/g, '').replace(/[^0-9]/g, '');
      const formateado = (limpio.match(/.{1,4}/g) || []).join(' ').substring(0, 23);
      if (formateado !== valor) {
        this.pagoForm.get('numeroTarjeta')!.setValue(formateado, { emitEvent: false });
      }
    });
  }

  private cargarReserva(): void {
    this.service.obtenerPorId(this.idReserva).subscribe({
      next: (data) => {
        this.reserva = data;
        this.cargando = false;
        // Si la reserva ya está pagada o no requiere pago, no pintamos pasarela
        if (!data.requierePago) {
          this.router.navigate(['/mis-reservas']);
        }
      },
      error: () => {
        this.cargando = false;
        this.mensajeError = 'No se pudo cargar la reserva.';
      },
    });
  }

  pagar(): void {
    if (this.pagoForm.invalid || !this.reserva) {
      this.pagoForm.markAllAsTouched();
      return;
    }

    this.mensajeError = null;
    this.procesando = true;

    const v = this.pagoForm.getRawValue();
    const dto: PagoRequest = {
      titular: v.titular,
      numeroTarjeta: v.numeroTarjeta,
      mesExp: v.mesExp as number,
      anioExp: v.anioExp as number,
      cvv: v.cvv,
    };

    // Pequeño retardo artificial para que se vea el "procesando" (UX),
    // el backend ya hace su parte de simulación con la validación.
    setTimeout(() => {
      this.service.pagar(this.idReserva, dto).subscribe({
        next: (pagada) => {
          this.procesando = false;

          Swal.fire({
            icon: 'success',
            title: '¡Pago realizado!',
            html: `
              <p style="margin: 8px 0 16px; color: #f0f0f5;">
                Tu reserva del <strong>${pagada.fechaReserva}</strong>
                de <strong>${pagada.horaInicio.substring(0, 5)}</strong>
                a <strong>${pagada.horaFin.substring(0, 5)}</strong>
                ha quedado confirmada.
              </p>
              <p style="font-size: 13px; color: #6b6b80; margin: 0;">
                Te hemos enviado un correo con los detalles y el código QR de acceso.
              </p>
            `,
            background: '#111114',
            color: '#f0f0f5',
            confirmButtonText: 'Ver mis reservas',
            confirmButtonColor: '#1a9fff',
            allowOutsideClick: false,
          }).then(() => {
            this.router.navigate(['/mis-reservas']);
          });
        },
        error: (err) => {
          this.procesando = false;
          this.mensajeError = err?.error?.message || err?.error || 'El pago no se pudo completar.';
        },
      });
    }, 800);
  }

  cancelarPago(): void {
    if (!confirm('¿Cancelar el pago? La reserva se eliminará.')) return;
    this.service.cancelarReserva(this.idReserva).subscribe({
      next: () => this.router.navigate(['/mis-reservas']),
      error: (err) => {
        this.mensajeError = err?.error?.message || 'No se pudo cancelar la reserva.';
      },
    });
  }

  rellenarTarjetaPrueba(numero: string): void {
    this.pagoForm.patchValue({
      titular: this.pagoForm.value.titular || 'USUARIO DE PRUEBA',
      numeroTarjeta: (numero.match(/.{1,4}/g) || []).join(' '),
      mesExp: 12,
      anioExp: new Date().getFullYear() + 2,
      cvv: '123',
    });
  }

  formatearPrecio(precio: number | null | undefined): string {
  if (precio == null) return '0,00 €';
  return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(precio);
}
}