import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ReservaService } from '../../../services/ReservaService';


@Component({
  selector: 'app-lista-reservas',
  standalone: true,
  imports: [CommonModule],
  providers: [DatePipe], // para formatear fechas en el HTML
  templateUrl: './lista-reservas.html',
  styleUrl: './lista-reservas.css'
})
export class ListaReservas implements OnInit {
  
  @Output() volver = new EventEmitter<void>();

  reservas: any[] = [];
  cargando: boolean = true;
  mensajeError: string = '';
  
  // Guardamos la fecha de hoy en formato YYYY-MM-DD para comparar facilmente
  hoyStr: string = new Date().toISOString().split('T')[0]; 

  constructor(private reservaService: ReservaService) {}

  ngOnInit(): void {
    this.cargarReservas();
  }

  cargarReservas() {
    const idPolideportivoString = sessionStorage.getItem('idPolideportivo');

    if (!idPolideportivoString) {
      this.mensajeError = 'Error: No se ha encontrado tu polideportivo asignado.';
      this.cargando = false;
      return;
    }

    const idPolideportivo = Number(idPolideportivoString);

    this.reservaService.obtenerReservasPorPolideportivo(idPolideportivo).subscribe({
      next: (data) => {
        this.procesarYOrdenarReservas(data);
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error cargando reservas', err);
        this.mensajeError = 'No se pudieron cargar las reservas del centro.';
        this.cargando = false;
      }
    });
  }

  procesarYOrdenarReservas(data: any[]) {
    // Las de hoy (ordenadas por hora de más pronto a más tarde)
    const reservasHoy = data.filter(r => r.fechaReserva === this.hoyStr)
      .sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));

    // Las futuras (ordenadas por fecha más cercana y luego por hora)
    const reservasFuturas = data.filter(r => r.fechaReserva > this.hoyStr)
      .sort((a, b) => {
        if (a.fechaReserva === b.fechaReserva) return a.horaInicio.localeCompare(b.horaInicio);
        return a.fechaReserva.localeCompare(b.fechaReserva);
      });

    //  Las pasadas (Historial: ordenadas por fecha más reciente)
    const reservasPasadas = data.filter(r => r.fechaReserva < this.hoyStr)
      .sort((a, b) => {
        if (a.fechaReserva === b.fechaReserva) return b.horaInicio.localeCompare(a.horaInicio);
        return b.fechaReserva.localeCompare(a.fechaReserva);
      });

    // Juntamos todo en el array final
    this.reservas = [...reservasHoy, ...reservasFuturas, ...reservasPasadas];
  }

  esHoy(fecha: string): boolean {
    return fecha === this.hoyStr;
  }
}