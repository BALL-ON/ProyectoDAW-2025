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

  /* variables de paginacion */
  paginaActual: number = 0;
  pageSize: number = 5;
  totalPaginas: number = 0;
  totalElementos: number = 0;
  
  // Guardamos la fecha de hoy en formato YYYY-MM-DD para comparar facilmente
  hoyStr: string = new Date().toISOString().split('T')[0]; 

  constructor(private reservaService: ReservaService) {}

  ngOnInit(): void {
    this.cargarReservas();
  }

  cargarReservas() {
    this.cargando = true;
    const idPolideportivoString = sessionStorage.getItem('idPolideportivo');

    if (!idPolideportivoString) {
      this.mensajeError = 'Error: No se ha encontrado tu polideportivo asignado.';
      this.cargando = false;
      return;
    }

    const idPolideportivo = Number(idPolideportivoString);

    this.reservaService.obtenerReservasPaginadas(idPolideportivo, this.paginaActual, this.pageSize).subscribe({
      next: (data) => {
        this.reservas = data.content; 
        
        this.totalPaginas = data.totalPages;
        this.totalElementos = data.totalElements;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar reservas paginadas', err);
        this.cargando = false;
      }
    });
  }

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginas) {
      this.paginaActual = nuevaPagina;
      this.cargarReservas();
    }
  }

  esHoy(fecha: string): boolean {
    return fecha === this.hoyStr;
  }
}