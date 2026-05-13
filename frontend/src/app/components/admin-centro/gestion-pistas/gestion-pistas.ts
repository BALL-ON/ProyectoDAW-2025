import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PistaService } from '../../../services/PistaService';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-gestion-pistas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './gestion-pistas.html',
  styleUrl: './gestion-pistas.css',
})
export class GestionPistas implements OnInit {
  pistas: any[] = [];
  cargando: boolean = true;
  mensajeExito: string = '';
  mensajeError: string = '';

  // Propiedades del formulario
  formularioPista: FormGroup;
  cargandoCrear: boolean = false;
  mensajeExitoCrear: string = '';
  mensajeErrorCrear: string = '';

  // Tipos de deporte disponibles
  tiposDeporte = [
    { id: 1, nombre: 'Tenis' },
    { id: 2, nombre: 'Pádel' },
    { id: 3, nombre: 'Fútbol' },
    { id: 4, nombre: 'Baloncesto' },
    { id: 5, nombre: 'Bádminton' },
    { id: 6, nombre: 'Voleibol' },
    { id: 7, nombre: 'Multideporte' },
  ];

  constructor(
    private pistaService: PistaService,
    private fb: FormBuilder,
  ) {
    this.formularioPista = this.fb.group({
      nombrePista: ['', [Validators.required, Validators.minLength(3)]],
      idTipoPista: ['', Validators.required],
      capacidad: [20, [Validators.min(1), Validators.max(500)]],
      precioHora: [0, [Validators.required, Validators.min(0)]],
      tiempoMinCancelacionHoras: [24, [Validators.min(0), Validators.max(168)]],
      requierePagoPrevio: [false],
      activa: [true],
    });
  }

  ngOnInit(): void {
    this.cargarPistas();
  }

  cargarPistas() {
    const idStr = sessionStorage.getItem('idPolideportivo');
    if (!idStr) {
      this.mensajeError = 'No se encontró el polideportivo asignado.';
      this.cargando = false;
      return;
    }

    // método que trae TODAS
    this.pistaService.obtenerTodasPistasPorPolideportivo(Number(idStr)).subscribe({
      next: (data) => {
        this.pistas = data;
        this.cargando = false;
      },
      error: (err) => {
        this.mensajeError = 'Error al cargar las pistas.';
        this.cargando = false;
      },
    });
  }

  toggleEstado(pista: any) {
    // Calculamos cuál va a ser el nuevo estado (el contrario al actual)
    const nuevoEstado = !pista.activa;

    pista.procesando = true;
    this.mensajeExito = '';
    this.mensajeError = '';

    // Llamamos al endpoint de tu compi
    this.pistaService.cambiarEstado(pista.idPista, nuevoEstado).subscribe({
      next: (pistaActualizada) => {
        // Actualizamos el estado en la tabla visualmente
        pista.activa = nuevoEstado;
        pista.procesando = false;

        const textoEstado = nuevoEstado ? 'activada' : 'desactivada (en mantenimiento)';
        this.mensajeExito = `La pista "${pista.nombrePista}" ha sido ${textoEstado}.`;
      },
      error: (err) => {
        pista.procesando = false;
        this.mensajeError = `Error al cambiar el estado de la pista "${pista.nombrePista}".`;
      },
    });

    //Confuguración para que la notificación desaparezca a los 10 segundos
    setTimeout(() => {
      this.mensajeExito = '';
      this.mensajeError = '';
    }, 10000);
  }

  crearPista() {
    if (this.formularioPista.invalid) {
      return;
    }

    const idStr = sessionStorage.getItem('idPolideportivo');
    if (!idStr) {
      this.mensajeErrorCrear = 'No se encontró el polideportivo asignado.';
      return;
    }

    this.cargandoCrear = true;
    this.mensajeExitoCrear = '';
    this.mensajeErrorCrear = '';

    const nuevaPista: any = new Object(this.formularioPista.value);
    nuevaPista.idPolideportivo = Number(idStr);

    this.pistaService.crear(nuevaPista).subscribe({
      next: (pistaCreada) => {
        this.cargandoCrear = false;
        this.mensajeExitoCrear = `La pista "${pistaCreada.nombrePista}" ha sido creada exitosamente.`;
        this.resetearFormulario();
        this.cargarPistas();
      },
      error: (err) => {
        this.cargandoCrear = false;
        this.mensajeErrorCrear =
          'Error al crear la pista. Verifica los datos e intenta nuevamente.';
      },
    });

    setTimeout(() => {
      this.mensajeExitoCrear = '';
      this.mensajeErrorCrear = '';
    }, 10000);
  }

  resetearFormulario() {
    this.formularioPista.reset({
      nombrePista: '',
      idTipoPista: '',
      capacidad: 20,
      precioHora: 0,
      tiempoMinCancelacionHoras: 24,
      requierePagoPrevio: false,
      activa: true,
    });
  }

  getErrorMessage(fieldName: string): string {
    const field = this.formularioPista.get(fieldName);

    if (field?.hasError('required')) {
      return 'Este campo es obligatorio.';
    }
    if (field?.hasError('minLength')) {
      return `Mínimo ${field.getError('minLength').requiredLength} caracteres.`;
    }
    if (field?.hasError('min')) {
      return `El valor mínimo es ${field.getError('min').min}.`;
    }
    if (field?.hasError('max')) {
      return `El valor máximo es ${field.getError('max').max}.`;
    }

    return 'Campo inválido.';
  }
}
