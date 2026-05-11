import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReservaService } from '../../../services/ReservaService';
import { PistaService } from '../../../services/PistaService';

@Component({
  selector: 'app-crear-reserva',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './crear-reserva.html',
  styleUrl: './crear-reserva.css'
})
export class crearReserva implements OnInit {
  
  // Esto es para avisarle al dashboard que cierre esta vista cuando acabe
  @Output() reservaCompletada = new EventEmitter<void>();

  reservaForm: FormGroup;
  mensajeExito: string = '';
  mensajeError: string = '';
  cargando: boolean = false;
  pistasDelCentro: any[] = [];
  cargandoPistas: boolean = true;

  constructor(private fb: FormBuilder, private reservaService: ReservaService, private pistaService: PistaService) {
    this.reservaForm = this.fb.group({
      idPista: ['', [Validators.required, Validators.min(1)]],
      fechaReserva: ['', Validators.required],
      horaInicio: ['', Validators.required],
      horaFin: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarPistas();
  }

  cargarPistas() {
    const idStr = sessionStorage.getItem('idPolideportivo');
    
    if (!idStr) {
      this.mensajeError = 'No se encontró el polideportivo asignado. Prueba a iniciar sesión de nuevo.';
      this.cargandoPistas = false;
      return;
    }

    const idPolideportivo = Number(idStr);

    this.pistaService.listarActivasPorPolideportivo(idPolideportivo).subscribe({
      next: (data) => {
        this.pistasDelCentro = data;
        this.cargandoPistas = false;
      },
      error: (err) => {
        console.error('Error al cargar las pistas', err);
        this.cargandoPistas = false;
        this.mensajeError = 'No se pudieron cargar las pistas del centro.';
      }
    });
  }

  crearReserva() {
    if (this.reservaForm.invalid) {
      this.reservaForm.markAllAsTouched();
      return;
    }

    this.cargando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    const datosReserva = this.reservaForm.value;

    this.reservaService.crearReserva(datosReserva).subscribe({
      next: (res) => {
        this.cargando = false;
        this.mensajeExito = '¡Pista bloqueada/reservada correctamente!';
        this.reservaForm.reset();

        setTimeout(() => {
          this.reservaCompletada.emit();
        }, 2000);
      },
      error: (err) => {
        this.cargando = false;
        this.mensajeError = err.error?.message || err.error || 'Error al crear la reserva';
      }
    });
  }

  campoInvalido(campo: string): boolean {
    const control = this.reservaForm.get(campo);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}