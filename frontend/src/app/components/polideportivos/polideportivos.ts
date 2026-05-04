import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PolideportivoService } from '../../services/PolideportivoService';
import { PolideportivoResponse } from '../../model/polideportivo.model';

@Component({
  selector: 'app-polideportivos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './polideportivos.html',
  styleUrl: './polideportivos.css',
})
export class Polideportivos implements OnInit {
  private readonly service = inject(PolideportivoService);
  private readonly router = inject(Router);

  todos: PolideportivoResponse[] = [];
  filtrados: PolideportivoResponse[] = [];

  filtroNombre = new FormControl<string>('', { nonNullable: true });
  filtroPoblacion = new FormControl<string>('', { nonNullable: true });

  cargando = false;
  errorCarga: string | null = null;

  ngOnInit(): void {
    this.cargar();

    // Reaccionamos a cambios en los filtros sin tener que pulsar nada
    this.filtroNombre.valueChanges.subscribe(() => this.aplicarFiltros());
    this.filtroPoblacion.valueChanges.subscribe(() => this.aplicarFiltros());
  }

  cargar(): void {
    this.cargando = true;
    this.errorCarga = null;
    this.service.listarTodos().subscribe({
      next: (data) => {
        // Orden alfabético por nombre, ignorando mayúsculas y acentos
        this.todos = [...data].sort((a, b) =>
          a.nombre.localeCompare(b.nombre, 'es', { sensitivity: 'base' })
        );
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: (err) => {
        console.error(err);
        this.errorCarga = 'No se han podido cargar los polideportivos.';
        this.cargando = false;
      },
    });
  }

  /** Filtros por nombre y población, case-insensitive y sin acentos */
  private aplicarFiltros(): void {
    const norm = (s: string) =>
      s.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');

    const fNombre = norm(this.filtroNombre.value.trim());
    const fPoblacion = norm(this.filtroPoblacion.value.trim());

    this.filtrados = this.todos.filter((p) => {
      const okNombre = !fNombre || norm(p.nombre).includes(fNombre);
      const okPobl = !fPoblacion || norm(p.poblacion).includes(fPoblacion);
      return okNombre && okPobl;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre.setValue('');
    this.filtroPoblacion.setValue('');
  }

  abrirPolideportivo(p: PolideportivoResponse): void {
    this.router.navigate(['/polideportivos', p.idPolideportivo, 'pistas']);
  }

  /** Etiqueta legible del método de pago para el chip */
  etiquetaMetodo(metodo: string): string {
    switch (metodo) {
      case 'Online':
        return 'Pago online';
      case 'Presencial':
        return 'Pago presencial';
      case 'Ambos':
        return 'Online y presencial';
      default:
        return metodo;
    }
  }
}
