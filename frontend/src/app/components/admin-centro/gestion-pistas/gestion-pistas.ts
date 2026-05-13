import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PistaService } from '../../../services/PistaService';

@Component({
  selector: 'app-gestion-pistas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-pistas.html',
  styleUrl: './gestion-pistas.css'
})
export class GestionPistas implements OnInit {
  
  pistas: any[] = [];
  cargando: boolean = true;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(private pistaService: PistaService) {}

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
      }
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
      }
    });

    //Confuguración para que la notificación desaparezca a los 10 segundos
    setTimeout(() => {
      this.mensajeExito = '';
      this.mensajeError = '';
    }, 10000);
  }
}
