import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservaResponse } from '../../model/reserva.model';
import { AuthService } from '../../services/auth';
import { ReservaService } from '../../services/ReservaService';

@Component({
  selector: 'app-mis-reservas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mis-reservas.html',
  styleUrl: './mis-reservas.css'
})
export class MisReservas implements OnInit {
  authService = inject(AuthService);
  reservaService = inject(ReservaService);

  // Variables de estado (señales)
  todasLasReservas = signal<ReservaResponse[]>([]);
  cargando = signal<boolean>(true);
  pestanaActiva = signal<'proximas' | 'pasadas'>('proximas');
  reservaAValorar = signal<number | null>(null);
  estrellasSeleccionadas = signal<number>(0);
  textoComentario = signal<string>('');

  // Filtra las próximas: no están canceladas y su hora exacta no ha pasado
  reservasProximas = computed(() => {
    const ahora = new Date();

    return this.todasLasReservas().filter(r => {
      // Si está cancelada, disfrutada o no asistida, fuera de próximas
      if (r.estadoReserva === 'Cancelada' || r.estadoReserva === 'Disfrutada' || r.estadoReserva === 'No_Asistido') {
        return false;
      }

      // Comparamos la hora exacta (Fecha + Hora)
      const fechaHoraReserva = new Date(`${r.fechaReserva}T${r.horaInicio}`);
      return fechaHoraReserva >= ahora;
    });
  });

  // Filtra las pasadas: o son canceladas/disfrutadas, o su hora exacta ya pasó
  reservasPasadas = computed(() => {
    const ahora = new Date();

    return this.todasLasReservas().filter(r => {
      // Si está en estos estados, va directo a pasadas
      if (r.estadoReserva === 'Cancelada' || r.estadoReserva === 'Disfrutada' || r.estadoReserva === 'No_Asistido') {
        return true;
      }

      // Comparamos la hora exacta (Fecha + Hora)
      const fechaHoraReserva = new Date(`${r.fechaReserva}T${r.horaInicio}`);
      return fechaHoraReserva < ahora;
    });
  });

  /**
   * Se ejecuta al cargar la página.
   * Verifica si el usuario está logueado. Si está pide sus reservas.
   */
  ngOnInit() {
    if (!this.authService.getToken()) {
      this.cargando.set(false);
      return;
    }
    this.cargarReservas();
  }

  /**
   * Llama al servicio de backend para obtener la lista de reservas.
   * Muestra el estado de carga mientras espera la respuesta.
   */
  cargarReservas() {
    this.cargando.set(true);
    this.reservaService.misReservas().subscribe({
      next: (datos) => {
        this.todasLasReservas.set(datos);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error cargando reservas', err);
        this.cargando.set(false);
      }
    });
  }

  /**
   * Cambia la pestaña activa entre proxims y pasadas.
   */
  cambiarPestana(pestana: 'proximas' | 'pasadas') {
    this.pestanaActiva.set(pestana);
  }

  formatearEstado(estado: string): string {
    if (!estado) return '';
    // Reemplaza los guiones bajos por espacios
    let formateado = estado.replace(/_/g, ' ');
    // Primera letra mayúscula, el resto minúscula
    return formateado.charAt(0).toUpperCase() + formateado.slice(1).toLowerCase();
  }

  /**
   * Comprueba si la reserva se puede cancelar (+24h de antelación)
   */
  puedeCancelar(fechaReserva: string, horaInicio: string): boolean {
    // Unimos la fecha y la hora que vienen del backend y la formateamos
    const hora = horaInicio.length === 5 ? `${horaInicio}:00` : horaInicio;
    // Creamos la fecha exacta del partido
    const fechaPartido = new Date(`${fechaReserva}T${hora}`);
    
    //Calculamos la diferencia en horas respecto a este mismo instante
    const ahora = new Date();
    const diferenciaMilisegundos = fechaPartido.getTime() - ahora.getTime();
    const horasDeDiferencia = diferenciaMilisegundos / (1000 * 60 * 60);

    // Si faltan más de 24 horas, devolvemos true (se puede cancelar)
    return horasDeDiferencia >= 24;
  }

  /**
   * Llama al backend para cancelar una reserva.
   * Si se cancela correctamente recarga la lista de reservas para actualizar la vista.
   */
  cancelarReserva(id: number) {
    if(confirm('¿Estás seguro de que deseas cancelar esta reserva?')) {
      this.reservaService.cancelarReserva(id).subscribe({
        next: () => {
          alert('Reserva cancelada correctamente.');
          this.cargarReservas(); 
        },
        error: (err) => {
          alert(err.error?.message || 'Error al cancelar la reserva.');
        }
      });
    }
  }

  /**
   * Permite dejar una reseña en la reserva disfrutada
   */
  dejarResena(idReserva: number) {
    this.reservaAValorar.set(idReserva);
    this.estrellasSeleccionadas.set(0);
  }

  /**
   * Cierra el modal de dejar reseña
   */
  cerrarModalResena() {
    this.reservaAValorar.set(null);
    this.estrellasSeleccionadas.set(0);
    this.textoComentario.set('');
  }

  /**
   * Permite valorar por puntos (estrellas) en la reseña
   */
  seleccionarEstrella(puntos: number) {
    this.estrellasSeleccionadas.set(puntos);
  }

  /**
   * Guarda reseña
   */
  enviarResena(comentarioTexto: string) {
    console.log('--- INICIO DE ENVÍO DE RESEÑA ---');
    console.log('1. Texto recibido desde el HTML:', comentarioTexto);

    const id = this.reservaAValorar();
    const puntos = this.estrellasSeleccionadas();

    console.log('2. Datos capturados -> ID Reserva:', id, '| Estrellas:', puntos);

    // Comprobación de seguridad
    if (!id || puntos === 0) {
      console.warn('⚠️ ABORTANDO: O no hay ID o las estrellas son 0.');
      return; // <-- Si el código entraba aquí antes, por eso no hacía nada
    }

    const payload = {
      idReserva: id,
      puntuacion: puntos,
      comentario: comentarioTexto.trim()
    };

    console.log('3. Todo correcto. Enviando este paquete al backend:', payload);

    // Llamada al servicio
    this.reservaService.crearResena(payload).subscribe({
      next: (respuesta) => {
        console.log('4. ✅ ¡ÉXITO! El backend ha respondido bien:', respuesta);
        alert('¡Gracias por valorar la pista!');
        this.cerrarModalResena();
        this.cargarReservas();
      },
      error: (err) => {
        console.error('4. ❌ ERROR: El backend ha devuelto un fallo:', err);
        const mensajeError = err.error?.message || 'Hubo un error al enviar tu reseña.';
        alert(mensajeError);
      }
    });
  }
}