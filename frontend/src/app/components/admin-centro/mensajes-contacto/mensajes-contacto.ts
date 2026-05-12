import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MensajeContacto } from '../../../services/mensaje-contacto';
import { MensajeDTO } from '../../../interfaces/mensaje-dto';

@Component({
  selector: 'app-mensajes-contacto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mensajes-contacto.html',
  styleUrl: './mensajes-contacto.css',
})
export class MensajesContacto implements OnInit {
  private mensajeServicio = inject(MensajeContacto);

  mensajes: MensajeDTO[] = [];
  cargando = false;
  filtro: 'todos' | 'pendientes' = 'todos';
  mensajeSeleccionado: MensajeDTO | null = null;
  busqueda = '';
  
  // Para confirmación de eliminación
  mostrarConfirmacion = false;
  mensajeAEliminar: MensajeDTO | null = null;

  ngOnInit(): void {
    this.cargarMensajes();
  }

  cargarMensajes(): void {
    this.cargando = true;
    
    if (this.filtro === 'pendientes') {
      this.mensajeServicio.listarPendientes().subscribe({
        next: (datos) => {
          this.mensajes = datos;
          this.cargando = false;
        },
        error: (err) => {
          console.error('Error al cargar mensajes pendientes:', err);
          this.cargando = false;
        },
      });
    } else {
      this.mensajeServicio.listarTodos().subscribe({
        next: (datos) => {
          this.mensajes = datos;
          this.cargando = false;
        },
        error: (err) => {
          console.error('Error al cargar todos los mensajes:', err);
          this.cargando = false;
        },
      });
    }
  }

  cambiarFiltro(nuevoFiltro: 'todos' | 'pendientes'): void {
    this.filtro = nuevoFiltro;
    this.cargarMensajes();
  }

  seleccionarMensaje(mensaje: MensajeDTO): void {
    this.mensajeSeleccionado = mensaje;
  }

  cerrarDetalle(): void {
    this.mensajeSeleccionado = null;
  }

  marcarComoLeido(mensaje: MensajeDTO): void {
    console.log('Estoy en marcarComoLeido con mensaje:', mensaje);
    console.log('ID del mensaje:', mensaje.idMensaje);
    if (mensaje.idMensaje) {
      this.mensajeServicio.marcarLeido(mensaje.idMensaje).subscribe({
        next: () => {
          mensaje.leido = true;
          console.log('Mensaje marcado como leído');
        },
        error: (err) => {
          console.error('Error al marcar como leído:', err);
        },
      });
    }
  }

  abrirConfirmacionEliminar(mensaje: MensajeDTO): void {
    this.mensajeAEliminar = mensaje;
    this.mostrarConfirmacion = true;
  }

  cancelarEliminacion(): void {
    this.mostrarConfirmacion = false;
    this.mensajeAEliminar = null;
  }

  confirmarEliminacion(): void {
    if (this.mensajeAEliminar && this.mensajeAEliminar.idMensaje) {
      this.mensajeServicio.eliminar(this.mensajeAEliminar.idMensaje).subscribe({
        next: () => {
          this.mensajes = this.mensajes.filter(
            (m) => m.idMensaje !== this.mensajeAEliminar!.idMensaje 
          );
          console.log('Mensaje eliminado correctamente');
          this.mostrarConfirmacion = false;
          this.mensajeAEliminar = null;
        },
        error: (err) => {
          console.error('Error al eliminar el mensaje:', err);
          this.mostrarConfirmacion = false;
        },
      });
    }
  }

  get mensajesFiltrados(): MensajeDTO[] {
    if (!this.busqueda.trim()) {
      return this.mensajes;
    }

    const busquedaLower = this.busqueda.toLowerCase();
    return this.mensajes.filter(
      (m) =>
        m.nombre?.toLowerCase().includes(busquedaLower) ||
        m.email?.toLowerCase().includes(busquedaLower) ||
        m.asunto?.toLowerCase().includes(busquedaLower) ||
        m.mensaje?.toLowerCase().includes(busquedaLower)
    );
  }

  get totalPendientes(): number {
    return this.mensajes.filter((m) => !m.leido).length;
  }

  obtenerEstadoBadge(mensaje: MensajeDTO): string {
    return mensaje.leido ? 'leido' : 'pendiente';
  }

  formatearFecha(fecha: string | Date | undefined): string {
    if (!fecha) return '--';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  }
}