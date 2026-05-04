import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PistaService } from '../../services/PistaService';
import { PolideportivoService } from '../../services/PolideportivoService';
import { PistaResponse } from '../../model/pista.model';
import { PolideportivoResponse } from '../../model/polideportivo.model';

@Component({
  selector: 'app-pistas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pistas.html',
  styleUrl: './pistas.css',
})
export class Pistas implements OnInit {
  private readonly pistaService = inject(PistaService);
  private readonly poliService = inject(PolideportivoService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  idPolideportivo!: number;
  polideportivo: PolideportivoResponse | null = null;

  todas: PistaResponse[] = [];
  filtradas: PistaResponse[] = [];

  filtroTipo = new FormControl<string>('', { nonNullable: true });

  cargandoPoli = false;
  cargandoPistas = false;
  errorCarga: string | null = null;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.errorCarga = 'No se ha indicado ningún polideportivo.';
      return;
    }
    this.idPolideportivo = Number(idParam);

    this.cargarPolideportivo();
    this.cargarPistas();

    this.filtroTipo.valueChanges.subscribe(() => this.aplicarFiltros());
  }

  // ─── Carga ──────────────────────────────────────────────────────────────

  private cargarPolideportivo(): void {
    this.cargandoPoli = true;
    this.poliService.obtenerPorId(this.idPolideportivo).subscribe({
      next: (data) => {
        this.polideportivo = data;
        this.cargandoPoli = false;
      },
      error: (err) => {
        console.error(err);
        this.cargandoPoli = false;
        this.errorCarga = 'No se ha podido cargar el polideportivo.';
      },
    });
  }

  private cargarPistas(): void {
    this.cargandoPistas = true;
    this.pistaService.listarActivasPorPolideportivo(this.idPolideportivo).subscribe({
      next: (data) => {
        this.todas = [...data].sort((a, b) =>
          a.nombrePista.localeCompare(b.nombrePista, 'es', { sensitivity: 'base' })
        );
        this.aplicarFiltros();
        this.cargandoPistas = false;
      },
      error: (err) => {
        console.error(err);
        this.errorCarga = 'No se han podido cargar las pistas de este centro.';
        this.cargandoPistas = false;
      },
    });
  }

  // ─── Filtros ────────────────────────────────────────────────────────────

  /** Tipos únicos disponibles, para el selector. */
  get tiposDisponibles(): string[] {
    const set = new Set(this.todas.map((p) => p.nombreTipoPista));
    return Array.from(set).sort((a, b) => a.localeCompare(b, 'es'));
  }

  private aplicarFiltros(): void {
    const tipo = this.filtroTipo.value;
    this.filtradas = !tipo
      ? [...this.todas]
      : this.todas.filter((p) => p.nombreTipoPista === tipo);
  }

  limpiarFiltros(): void {
    this.filtroTipo.setValue('');
  }

  // ─── Navegación ─────────────────────────────────────────────────────────

  /** Click en una pista → página de reserva de esa pista. */
  reservarPista(pista: PistaResponse): void {
    this.router.navigate(['/reserva', pista.idPista]);
  }

  volverAlListado(): void {
    this.router.navigate(['/polideportivos']);
  }

  /** Icono según el tipo de pista (puro azúcar visual). */
  iconoPorTipo(nombreTipo: string): string {
    const t = nombreTipo.toLowerCase();
    if (t.includes('fútbol') || t.includes('futbol')) return '⚽';
    if (t.includes('pádel') || t.includes('padel')) return '🎾';
    if (t.includes('tenis')) return '🎾';
    if (t.includes('basket') || t.includes('balonces')) return '🏀';
    if (t.includes('volley') || t.includes('vóley') || t.includes('voley')) return '🏐';
    if (t.includes('balonmano')) return '🤾';
    if (t.includes('hockey')) return '🏑';
    return '🏟️';
  }
}
