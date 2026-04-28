import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ReservaService } from '../../services/ReservaService';
import { Reserva as IReserva } from '../../model/reserva.model';

@Component({
  selector: 'app-reserva',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reserva.html',
  styleUrl: './reserva.css',
})
export class Reserva implements OnInit {
  reservasExistentes: any[] = [];
  idEdicion: number | null = null;
  minDate: string = '';
  
  timeSlots = [
    { id: 8, label: '08:00 - 09:00', start: '08:00', end: '09:00' },
    { id: 9, label: '09:00 - 10:00', start: '09:00', end: '10:00' },
    { id: 10, label: '10:00 - 11:00', start: '10:00', end: '11:00' },
    { id: 11, label: '11:00 - 12:00', start: '11:00', end: '12:00' },
    { id: 12, label: '12:00 - 13:00', start: '12:00', end: '13:00' },
    { id: 13, label: '13:00 - 14:00', start: '13:00', end: '14:00' },
    { id: 14, label: '14:00 - 15:00', start: '14:00', end: '15:00' },
    { id: 15, label: '15:00 - 16:00', start: '15:00', end: '16:00' },
    { id: 16, label: '16:00 - 17:00', start: '16:00', end: '17:00' },
    { id: 17, label: '17:00 - 18:00', start: '17:00', end: '18:00' },
    { id: 18, label: '18:00 - 19:00', start: '18:00', end: '19:00' },
    { id: 19, label: '19:00 - 20:00', start: '19:00', end: '20:00' },
    { id: 20, label: '20:00 - 21:00', start: '20:00', end: '21:00' },
    { id: 21, label: '21:00 - 22:00', start: '21:00', end: '22:00' }
  ];

  startSlot: any = null;
  endSlot: any = null;

  reservaForm = new FormGroup({
    fecha: new FormControl('', [Validators.required]),
    hora: new FormControl<any>(null, [Validators.required])
  });

  constructor(private service: ReservaService, private cd: ChangeDetectorRef) {}

  ngOnInit() {
    // Validación de antelación de un día requerida por el backend
    const mañana = new Date();
    mañana.setDate(mañana.getDate() + 1);
    this.minDate = mañana.toISOString().split('T')[0];

    this.obtenerDatos();
  }

  obtenerDatos() {
    this.service.buscarReservas().subscribe({
      next: (data: any[]) => {
        // Ordenar datos
        const ordenadas = data.sort((a, b) => {
          if (a.estado === 'FINALIZADA' && b.estado !== 'FINALIZADA') return 1;
          if (a.estado !== 'FINALIZADA' && b.estado === 'FINALIZADA') return -1;
          return 0;
        });

        // 1. Cambiamos la referencia del array
        this.reservasExistentes = [...ordenadas];
        
        // 2. Forzamos a Angular a revisar la vista inmediatamente
        this.cd.detectChanges(); 
        
        console.log('Interfaz actualizada');
      }
    });
  }

  seleccionarSlot(slot: any) {
    if (!this.startSlot || (this.startSlot && this.endSlot)) {
      this.startSlot = slot;
      this.endSlot = null;
    } else {
      const startIndex = this.timeSlots.indexOf(this.startSlot);
      const endIndex = this.timeSlots.indexOf(slot);

      if (endIndex >= startIndex && endIndex - startIndex < 3) {
        // Verificar si hay huecos ocupados en medio
        for (let i = startIndex; i <= endIndex; i++) {
          if (this.estaOcupado(this.timeSlots[i])) {
            alert("El rango seleccionado contiene horas ya reservadas.");
            this.limpiarSeleccion();
            return;
          }
        }
        this.endSlot = slot;
      } else {
        this.startSlot = slot;
        this.endSlot = null;
      }
    }
    // Actualizamos el valor del formulario para que sea válido
    this.reservaForm.patchValue({ hora: this.startSlot });
  }

  hayOcupadosEnMedio(idInicio: number, idFin: number): boolean {
    for (let i = idInicio; i <= idFin; i++) {
      const slot = this.timeSlots.find(s => s.id === i);
      if (slot && this.estaOcupado(slot)) return true;
    }
    return false;
  }

  estaSeleccionado(slot: any): boolean {
    if (!this.startSlot) return false;
    const startIndex = this.timeSlots.indexOf(this.startSlot);
    const endIndex = this.endSlot ? this.timeSlots.indexOf(this.endSlot) : startIndex;
    const currentIndex = this.timeSlots.indexOf(slot);
    return currentIndex >= startIndex && currentIndex <= endIndex;
  }

  estaOcupado(slot: any): boolean {
    const fecha = this.reservaForm.get('fecha')?.value;
    if (!fecha) return false;
    
    const slotInicio = `${fecha}T${slot.start}:00`;
    const slotFin = `${fecha}T${slot.end}:00`;

    return this.reservasExistentes.some(res => 
      // Un slot está ocupado si se solapa con cualquier reserva existente
      slotInicio < res.fechaHoraFin && slotFin > res.fechaHoraInicio
    );
  }

  limpiarSeleccion() {
    this.startSlot = null;
    this.endSlot = null;
    this.reservaForm.get('hora')?.reset();
  }

  borrar(id: number) {
    if (confirm('¿Seguro que quieres cancelar esta reserva?')) {
      // Cambiamos el subscribe para que sea un objeto con la función next
      this.service.eliminarReserva(id).subscribe({
        next: () => {
          // 1. Refrescar la tabla y los botones de horas
          this.obtenerDatos();
          this.limpiarSeleccion(); 
        },
        error: (err) => {
          console.error('Error al borrar:', err);
          alert('No se pudo eliminar la reserva');
        }
      });
    }
  }

  cargarParaEditar(reserva: any) {
    // 1. Ponemos la fecha en el formulario (aunque no la dejaremos cambiar)
    const fecha = reserva.fechaHoraInicio.split('T')[0];
    this.reservaForm.patchValue({ fecha: fecha });
    
    // 2. Avisamos al usuario que elija el nuevo rango arriba
    alert("Selecciona el nuevo horario en el panel superior para la fecha " + fecha);
    
    // Guardamos el ID que estamos editando para que el botón "Confirmar" sepa que es un PUT y no un POST
    this.idEdicion = reserva.id;
  }

  enviar() {
    const fecha = this.reservaForm.get('fecha')?.value;
  
    // Si hay fecha y al menos un slot inicial seleccionado
    if (fecha && this.startSlot) {
    
      // Determinamos cuál es el último slot del rango (si no hay endSlot, es el mismo startSlot)
      const ultimoSlot = this.endSlot ? this.endSlot : this.startSlot;

      const nuevaReserva = {
        // Tomamos la hora de inicio del primer slot seleccionado
        fechaHoraInicio: `${fecha}T${this.startSlot.start}:00`,
        // Tomamos la hora de fin del ÚLTIMO slot seleccionado
        fechaHoraFin: `${fecha}T${ultimoSlot.end}:00`,
        estado: 'CONFIRMADA'
      };

      console.log('Enviando reserva de rango:', nuevaReserva);

      this.service.crearReserva(nuevaReserva).subscribe({
        next: () => {
          this.limpiarSeleccion(); // Limpiamos slots y formulario
          this.obtenerDatos();     // Refrescamos la tabla
        },
        error: (err) => {
          alert(err.error || 'Error al procesar la reserva');
      }
    });
  }
  }

  // Función auxiliar para no repetir código de limpieza
  finalizarProceso() {
    this.reservaForm.reset();
    this.startSlot = null;
    this.endSlot = null;
    this.idEdicion = null; 
    this.obtenerDatos();
  }
}