import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PistaService } from '../../../services/PistaService';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TipoPistaService } from '../../../services/tipo-pista-service';
import { PistaRequest } from '../../../model/pista.model';

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

  // Tipos de pista disponibles
  tiposPista: any[] = [];

  //variable para saber si el precio de la reserva es 0 o no, para dejar checkeable el checkbox de "requiere pago previo" o no
  visibleRequierePagoPrevio: boolean = false;

  constructor(
    private pistaService: PistaService,
    private fb: FormBuilder,
    private tipoPistaService: TipoPistaService,
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
    this.cargarTiposPista();
    this.cambiarVisibilidadRequierePagoPrevio();

    this.formularioPista.get('precioHora')?.valueChanges.subscribe(precio => {
      this.cambiarVisibilidadRequierePagoPrevio();
    });
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

  cargarTiposPista() {
    this.tipoPistaService.listarTodos().subscribe({
      next: (tipos) => {
        this.tiposPista = tipos;
      },
      error: (err) => {
        this.mensajeError = 'Error al cargar los tipos de pista.';
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
    }, 4000);
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

    const nuevaPista: PistaRequest = {
      idPolideportivo: Number(idStr),
      idTipoPista: this.formularioPista.value.idTipoPista,
      nombrePista: this.formularioPista.value.nombrePista,
      capacidad: this.formularioPista.value.capacidad,
      precioHora: this.formularioPista.value.precioHora,
      tiempoMinCancelacionHoras: this.formularioPista.value.tiempoMinCancelacionHoras,
      requierePagoPrevio: this.formularioPista.value.requierePagoPrevio,
      activa: this.formularioPista.value.activa,
    };

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
    }, 4000);
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

  getErrorMessage(campoName: string): string {
    const campo = this.formularioPista.get(campoName);

    if (campo?.hasError('required')) {
      return 'Este campo es obligatorio.';
    }
    if (campo?.hasError('minLength')) {
      return `Mínimo ${campo.getError('minLength').requiredLength} caracteres.`;
    }
    if (campo?.hasError('min')) {
      return `El valor mínimo es ${campo.getError('min').min}.`;
    }
    if (campo?.hasError('max')) {
      return `El valor máximo es ${campo.getError('max').max}.`;
    }

    return 'Campo inválido.';
  }

  cambiarVisibilidadRequierePagoPrevio() {
    const precioHora = this.formularioPista.get('precioHora')?.value;
    this.visibleRequierePagoPrevio = precioHora > 0;

    if (!this.visibleRequierePagoPrevio) {
      this.formularioPista.get('requierePagoPrevio')?.setValue(false);
      this.formularioPista.get('requierePagoPrevio')?.disable();
    } else {
      this.formularioPista.get('requierePagoPrevio')?.enable();
    }
  }
}
