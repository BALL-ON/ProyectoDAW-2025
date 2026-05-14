import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReservaService } from '../../services/ReservaService';
import { HorarioService } from '../../services/HorarioService';
import {
  OcupacionSlot,
  ReservaRequest,
  ReservaResponse,
  SlotHorario,
} from '../../model/reserva.model';
import { DiaSemana, HorarioPistaResponse } from '../../model/horario.model';

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
  private readonly horarioService = inject(HorarioService);
  private readonly route = inject(ActivatedRoute);
  private readonly cd = inject(ChangeDetectorRef);
  private readonly router = inject(Router);

  // Pista que estamos reservando (viene como ruta /reserva/:idPista)
  idPista!: number;

  // Estado del formulario
  reservaForm = new FormGroup({
    fecha: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] }),
  });

  minDate = '';

  // Slots horarios del día seleccionado (se regeneran al cambiar fecha)
  timeSlots: SlotHorario[] = [];

  // Horarios semanales de la pista (cargados del backend, uno por día abierto)
  horarios: HorarioPistaResponse[] = [];
  horariosCargados = false;

  // True si la pista no abre el día seleccionado (no existe horario para ese día)
  diaCerrado = false;
  // Texto resumen del horario del día seleccionado, p.ej. "08:00 - 22:00"
  horarioDelDiaTexto = '';

  // Selección actual del usuario
  startSlot: SlotHorario | null = null;
  endSlot: SlotHorario | null = null;

  // Datos cargados desde el backend
  ocupacion: OcupacionSlot[] = [];
  misReservas: ReservaResponse[] = [];

  // Estado UI
  cargandoOcupacion = false;
  enviando = false;
  mensajeError: string | null = null;
  mensajeOk: string | null = null;

  // Mapa día JS (0=domingo) → enum del backend
  private readonly DIA_SEMANA_MAP: Record<number, DiaSemana> = {
    0: 'Domingo',
    1: 'Lunes',
    2: 'Martes',
    3: 'Miercoles',
    4: 'Jueves',
    5: 'Viernes',
    6: 'Sabado',
  };

  ngOnInit(): void {
    // 1. Sacamos idPista de la ruta /reserva/:idPista
    const idParam = this.route.snapshot.paramMap.get('idPista');
    if (!idParam) {
      this.mensajeError = 'No se ha indicado ninguna pista.';
      return;
    }
    this.idPista = Number(idParam);

    // 2. Fecha mínima = mañana (validación de antelación de 1 día)
    const manana = new Date();
    manana.setDate(manana.getDate() + 1);
    this.minDate = manana.toISOString().split('T')[0];

    // 3. Cargar horarios de la pista y el historial del usuario
    this.cargarHorarios();
    this.cargarMisReservas();

    // 4. Cuando cambie la fecha, recargar la ocupación y regenerar slots
    this.reservaForm.get('fecha')!.valueChanges.subscribe((fecha) => {
      this.limpiarSeleccion();
      if (fecha) {
        this.actualizarSlotsDelDia(fecha);
        if (!this.diaCerrado) {
          this.cargarOcupacion(fecha);
        } else {
          this.ocupacion = [];
        }
      } else {
        this.timeSlots = [];
        this.ocupacion = [];
        this.horarioDelDiaTexto = '';
        this.diaCerrado = false;
      }
    });
  }

  // ─── Carga de datos ───────────────────────────────────────────────────────

  private cargarHorarios(): void {
    this.horarioService.listarPorPista(this.idPista).subscribe({
      next: (data) => {
        this.horarios = data;
        this.horariosCargados = true;
        // Si el usuario ya había seleccionado fecha antes de que llegasen
        // los horarios, regeneramos los slots con la info correcta.
        const fecha = this.reservaForm.get('fecha')!.value;
        if (fecha) {
          this.actualizarSlotsDelDia(fecha);
        }
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar horarios de la pista', err);
        this.horariosCargados = true; // permitimos que la UI siga, pero sin horarios
      },
    });
  }

  private cargarMisReservas(): void {
    this.service.misReservasEnPista(this.idPista).subscribe({
      next: (data) => {
        // Confirmadas primero, luego por fecha+hora descendente
        this.misReservas = [...data].sort((a, b) => {
          const pesoA = a.estadoReserva === 'Confirmada' ? 0 : 1;
          const pesoB = b.estadoReserva === 'Confirmada' ? 0 : 1;
          if (pesoA !== pesoB) return pesoA - pesoB;
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

  /**
   * Regenera los timeSlots según el horario que tenga la pista para el día
   * de la semana correspondiente a la fecha elegida.
   * Si no hay horario para ese día, marca diaCerrado y vacía la rejilla.
   */
  private actualizarSlotsDelDia(fecha: string): void {
    // Si los horarios aún no han llegado, no podemos saber si está cerrado.
    // No marcamos diaCerrado para evitar un falso positivo durante la carga.
    if (!this.horariosCargados) {
      this.timeSlots = [];
      this.diaCerrado = false;
      this.horarioDelDiaTexto = '';
      return;
    }

    // Parseamos "YYYY-MM-DD" en local para evitar líos de zona horaria
    const [y, m, d] = fecha.split('-').map(Number);
    const jsDay = new Date(y, m - 1, d).getDay();
    const dia = this.DIA_SEMANA_MAP[jsDay];

    const horario = this.horarios.find((h) => h.diaSemana === dia);

    if (!horario) {
      this.timeSlots = [];
      this.diaCerrado = true;
      this.horarioDelDiaTexto = '';
      return;
    }

    this.diaCerrado = false;
    const inicio = parseInt(horario.horaInicio.substring(0, 2), 10);
    // Si la hora de fin tiene minutos (p.ej. 23:30) redondeamos hacia abajo,
    // porque generamos slots de 1h enteros.
    const fin = parseInt(horario.horaFin.substring(0, 2), 10);

    this.timeSlots = this.generarSlots(inicio, fin);
    this.horarioDelDiaTexto =
      `${horario.horaInicio.substring(0, 5)} - ${horario.horaFin.substring(0, 5)}`;
  }

  // ─── Lógica de selección de slots ─────────────────────────────────────────

  /** Un slot está ocupado si su rango se solapa con cualquier rango de la ocupación. */
  estaOcupado(slot: SlotHorario): boolean {
    return this.ocupacion.some(
      (oc) => slot.start < oc.horaFin.substring(0, 5) && slot.end > oc.horaInicio.substring(0, 5)
    );
  }

  estaSeleccionado(slot: SlotHorario): boolean {
    if (!this.startSlot) return false;
    const startIdx = this.timeSlots.indexOf(this.startSlot);
    const endIdx = this.endSlot ? this.timeSlots.indexOf(this.endSlot) : startIdx;
    const idx = this.timeSlots.indexOf(slot);
    return idx >= startIdx && idx <= endIdx;
  }

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

    if (startIdx === endIdx) {
      this.startSlot = null;
      this.endSlot = null;
      return;
    }

    if (endIdx < startIdx) {
      this.startSlot = slot;
      this.endSlot = null;
      return;
    }

    // Máximo 3 horas
    if (endIdx - startIdx >= 3) {
      this.mensajeError = 'Sólo puedes reservar un máximo de 3 horas seguidas.';
      return;
    }

    // Sin huecos ocupados en medio
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
        if (creada.requierePago) {
          this.router.navigate(['/pago', creada.idReserva]);
          return;
        }
        this.mensajeOk = `Reserva confirmada el ${creada.fechaReserva} de ${creada.horaInicio.substring(0, 5)} a ${creada.horaFin.substring(0, 5)}.`;
        this.limpiarSeleccion();
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

  resumenRango(): string {
    if (!this.startSlot) return '';
    const fin = this.endSlot ?? this.startSlot;
    return `${this.startSlot.start} - ${fin.end}`;
  }

  horasSeleccionadas(): number {
    if (!this.startSlot) return 0;
    const startIdx = this.timeSlots.indexOf(this.startSlot);
    const endIdx = this.endSlot ? this.timeSlots.indexOf(this.endSlot) : startIdx;
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
