import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ReservaService } from '../../services/ReservaService';
import {
  OcupacionSlot,
  ReservaRequest,
  ReservaResponse,
  SlotHorario,
} from '../../model/reserva.model';

@Component({
  selector: 'app-reserva',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reserva.html',
  styleUrl: './reserva.css',
})
export class Reserva implements OnInit {
  // Servicios inyectados
  private readonly service = inject(ReservaService);
  private readonly route = inject(ActivatedRoute);
  private readonly cd = inject(ChangeDetectorRef);

  // Pista que estamos reservando (viene como ruta /reserva/:idPista)
  idPista!: number;

  // Estado del formulario
  reservaForm = new FormGroup({
    fecha: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
  });

  minDate = '';

  // Catálogo de slots: horario de apertura del polideportivo
  // (08:00 - 22:00, en bloques de 1h). Si en el futuro queréis bloques de
  // 90 min como dice el RF_07, sólo hay que regenerar este array.
  readonly timeSlots: SlotHorario[] = this.generarSlots(8, 22);

  // Selección actual del usuario
  startSlot: SlotHorario | null = null;
  endSlot: SlotHorario | null = null;

  // Datos cargados desde el backend
  ocupacion: OcupacionSlot[] = [];     // slots ocupados de la pista en la fecha elegida
  misReservas: ReservaResponse[] = []; // historial del usuario en esta pista

  // Estado UI
  cargandoOcupacion = false;
  enviando = false;
  mensajeError: string | null = null;
  mensajeOk: string | null = null;

  ngOnInit(): void {
    // 1. Sacamos idPista de la ruta /reserva/:idPista
    const idParam = this.route.snapshot.paramMap.get('idPista');
    if (!idParam) {
      this.mensajeError = 'No se ha indicado ninguna pista.';
      return;
    }
    this.idPista = Number(idParam);

    // 2. Fecha mínima = mañana (mantengo tu validación de antelación de 1 día)
    const manana = new Date();
    manana.setDate(manana.getDate() + 1);
    this.minDate = manana.toISOString().split('T')[0];

    // 3. Cargar el historial del usuario en esta pista
    this.cargarMisReservas();

    // 4. Cuando cambie la fecha, recargar la ocupación
    this.reservaForm.get('fecha')!.valueChanges.subscribe((fecha) => {
      this.limpiarSeleccion();
      if (fecha) {
        this.cargarOcupacion(fecha);
      } else {
        this.ocupacion = [];
      }
    });
  }

  // ─── Carga de datos ───────────────────────────────────────────────────────

  private cargarMisReservas(): void {
    this.service.misReservasEnPista(this.idPista).subscribe({
      next: (data) => {
        // Ordenar: futuras/confirmadas primero, finalizadas/canceladas al final
        this.misReservas = [...data].sort((a, b) => {
          const pesoA = a.estadoReserva === 'Confirmada' ? 0 : 1;
          const pesoB = b.estadoReserva === 'Confirmada' ? 0 : 1;
          if (pesoA !== pesoB) return pesoA - pesoB;
          // Dentro del mismo grupo, por fecha+hora descendente (más reciente arriba)
          const claveA = `${a.fechaReserva}T${a.horaInicio}`;
          const claveB = `${b.fechaReserva}T${b.horaInicio}`;
          return claveB.localeCompare(claveA);
        });
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error al cargar mis reservas', err),
    });
  }

  private cargarOcupacion(fecha: string): void {
    this.cargandoOcupacion = true;
    this.service.ocupacionDelDia(this.idPista, fecha).subscribe({
      next: (data) => {
        this.ocupacion = data;
        this.cargandoOcupacion = false;
      },
      error: (err) => {
        console.error('Error al cargar ocupación', err);
        this.ocupacion = [];
        this.cargandoOcupacion = false;
      },
    });
  }

  // ─── Lógica de selección de slots ─────────────────────────────────────────

  /**
   * Un slot está ocupado si su rango [start, end) se solapa
   * con cualquier rango de la lista de ocupación.
   */
  estaOcupado(slot: SlotHorario): boolean {
    return this.ocupacion.some(
      (oc) => slot.start < oc.horaFin.substring(0, 5) && slot.end > oc.horaInicio.substring(0, 5)
    );
  }

  /**
   * Devuelve true si el slot está dentro del rango actualmente seleccionado.
   */
  estaSeleccionado(slot: SlotHorario): boolean {
    if (!this.startSlot) return false;
    const startIdx = this.timeSlots.indexOf(this.startSlot);
    const endIdx = this.endSlot
      ? this.timeSlots.indexOf(this.endSlot)
      : startIdx;
    const idx = this.timeSlots.indexOf(slot);
    return idx >= startIdx && idx <= endIdx;
  }

  /**
   * Maneja el clic en un slot. Permite rangos contiguos de hasta 3h.
   */
  seleccionarSlot(slot: SlotHorario): void {
    this.mensajeError = null;

    // Caso 1: no hay nada seleccionado o ya había rango cerrado → empezar de nuevo
    if (!this.startSlot || (this.startSlot && this.endSlot)) {
      this.startSlot = slot;
      this.endSlot = null;
      return;
    }

    // Caso 2: ya hay startSlot, este clic intenta cerrar el rango
    const startIdx = this.timeSlots.indexOf(this.startSlot);
    const endIdx = this.timeSlots.indexOf(slot);

    // Si clica el mismo slot, lo deselecciona
    if (startIdx === endIdx) {
      this.startSlot = null;
      this.endSlot = null;
      return;
    }

    // Si clica anterior al inicio, se reinicia con ese como nuevo inicio
    if (endIdx < startIdx) {
      this.startSlot = slot;
      this.endSlot = null;
      return;
    }

    // Validación: máximo 3 horas (índices 0, 1, 2 = 3 slots de 1h)
    if (endIdx - startIdx >= 3) {
      this.mensajeError = 'Sólo puedes reservar un máximo de 3 horas seguidas.';
      return;
    }

    // Validación: que no haya huecos ocupados en medio
    for (let i = startIdx; i <= endIdx; i++) {
      if (this.estaOcupado(this.timeSlots[i])) {
        this.mensajeError = 'El rango contiene horas ya reservadas. Elige otro tramo.';
        this.limpiarSeleccion();
        return;
      }
    }

    this.endSlot = slot;
  }

  limpiarSeleccion(): void {
    this.startSlot = null;
    this.endSlot = null;
    this.mensajeError = null;
    this.mensajeOk = null;
  }

  // ─── Envío ────────────────────────────────────────────────────────────────

  enviar(): void {
    this.mensajeError = null;
    this.mensajeOk = null;

    const fecha = this.reservaForm.get('fecha')!.value;
    if (!fecha || !this.startSlot) {
      this.mensajeError = 'Selecciona una fecha y un horario.';
      return;
    }

    const ultimoSlot = this.endSlot ?? this.startSlot;

    const dto: ReservaRequest = {
      idPista: this.idPista,
      fechaReserva: fecha,
      horaInicio: `${this.startSlot.start}:00`,
      horaFin: `${ultimoSlot.end}:00`,
    };

    this.enviando = true;
    this.service.crearReserva(dto).subscribe({
      next: (creada) => {
        this.enviando = false;
        this.mensajeOk = `Reserva confirmada el ${creada.fechaReserva} de ${creada.horaInicio.substring(0, 5)} a ${creada.horaFin.substring(0, 5)}.`;
        this.limpiarSeleccion();
        // Refrescar grid de ocupación y tabla de mis reservas
        this.cargarOcupacion(fecha);
        this.cargarMisReservas();
      },
      error: (err) => {
        this.enviando = false;
        this.mensajeError = err?.error?.message || err?.error || 'No se pudo crear la reserva.';
      },
    });
  }

  borrar(reserva: ReservaResponse): void {
    if (reserva.estadoReserva !== 'Confirmada') return;
    if (!confirm('¿Seguro que quieres cancelar esta reserva?')) return;

    this.service.cancelarReserva(reserva.idReserva).subscribe({
      next: () => {
        this.cargarMisReservas();
        const fecha = this.reservaForm.get('fecha')!.value;
        if (fecha) this.cargarOcupacion(fecha);
      },
      error: (err) => {
        this.mensajeError = err?.error?.message || 'No se pudo cancelar la reserva.';
      },
    });
  }

  // ─── Helpers de plantilla ────────────────────────────────────────────────

  /** Texto resumen del rango seleccionado */
  resumenRango(): string {
    if (!this.startSlot) return '';
    const fin = this.endSlot ?? this.startSlot;
    return `${this.startSlot.start} - ${fin.end}`;
  }

  /** Número de horas seleccionadas (para mostrar al usuario el coste estimado, etc.) */
  horasSeleccionadas(): number {
    if (!this.startSlot) return 0;
    const startIdx = this.timeSlots.indexOf(this.startSlot);
    const endIdx = this.endSlot
      ? this.timeSlots.indexOf(this.endSlot)
      : startIdx;
    return endIdx - startIdx + 1;
  }

  // ─── Util ────────────────────────────────────────────────────────────────

  private generarSlots(horaInicio: number, horaFin: number): SlotHorario[] {
    const slots: SlotHorario[] = [];
    for (let h = horaInicio; h < horaFin; h++) {
      const start = `${String(h).padStart(2, '0')}:00`;
      const end = `${String(h + 1).padStart(2, '0')}:00`;
      slots.push({ id: h, label: `${start} - ${end}`, start, end });
    }
    return slots;
  }
}
