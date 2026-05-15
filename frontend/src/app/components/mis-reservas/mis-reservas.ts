import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReservaResponse, QrReserva } from '../../model/reserva.model';
import { AuthService } from '../../services/auth';
import { ReservaService } from '../../services/ReservaService';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

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
  private router = inject(Router);

  // Variables de estado (señales)
  todasLasReservas = signal<ReservaResponse[]>([]);
  cargando = signal<boolean>(true);
  pestanaActiva = signal<'proximas' | 'pasadas'>('proximas');
  reservaAValorar = signal<number | null>(null);
  estrellasSeleccionadas = signal<number>(0);
  textoComentario = signal<string>('');

  // Modal del QR
  qrAbierto = signal<{ imagenBase64: string; reserva: ReservaResponse } | null>(null);
  cargandoQr = signal<boolean>(false);

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
   * Llama al backend para cancelar una reserva con confirmación previa
   * y mensaje de éxito que avisa del reembolso si estaba pagada.
   */
  cancelarReserva(reserva: ReservaResponse) {
    Swal.fire({
      title: '¿Cancelar esta reserva?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, cancelar',
      cancelButtonText: 'Volver',
      confirmButtonColor: '#ff4d4d',
      cancelButtonColor: '#6b6b80',
      background: '#111114',
      color: '#f0f0f5',
    }).then((result) => {
      if (!result.isConfirmed) return;

      const eraPagada = reserva.estadoPago === 'Pagado';

      this.reservaService.cancelarReserva(reserva.idReserva).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Reserva cancelada',
            html: `
              <p style="margin: 8px 0 ${eraPagada ? '16px' : '0'}; color: #f0f0f5;">
                Tu reserva del <strong>${reserva.fechaReserva}</strong>
                de <strong>${reserva.horaInicio.substring(0, 5)}</strong>
                a <strong>${reserva.horaFin.substring(0, 5)}</strong>
                se ha cancelado correctamente.
              </p>
              ${eraPagada ? `
                <p style="font-size: 13px; color: #6b6b80; margin: 0;">
                  Se reembolsará el importe al método de pago original en unos días hábiles.
                </p>
              ` : ''}
            `,
            background: '#111114',
            color: '#f0f0f5',
            confirmButtonText: 'Entendido',
            confirmButtonColor: '#1a9fff',
          });

          this.cargarReservas();
        },
        error: (err) => {
          Swal.fire({
            icon: 'error',
            title: 'No se pudo cancelar',
            text: err?.error?.message || 'Ha ocurrido un error al cancelar la reserva.',
            background: '#111114',
            color: '#f0f0f5',
            confirmButtonColor: '#1a9fff',
          });
        }
      });
    });
  }

  /**
   * Permite dejar una reseña en la reserva disfrutada
   */
  dejarResena(idReserva: number) {
    this.reservaAValorar.set(idReserva);
    this.estrellasSeleccionadas.set(0);
    document.documentElement.style.overflow = 'hidden'; // Bloquea el scroll de la página de fondo
  }

  /**
   * Cierra el modal de dejar reseña
   */
  cerrarModalResena() {
    this.reservaAValorar.set(null);
    this.estrellasSeleccionadas.set(0);
    this.textoComentario.set('');
    document.documentElement.style.overflow = 'auto';
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
        Swal.fire('¡Gracias por valorar la pista!');
        this.cerrarModalResena();
        this.cargarReservas();
      },
      error: (err) => {
        console.error('4. ❌ ERROR: El backend ha devuelto un fallo:', err);
        const mensajeError = err.error?.message || 'Hubo un error al enviar tu reseña.';
        Swal.fire(mensajeError);
      }
    });
  }

  /**
 * Abre el modal con el QR de la reserva. Pide la imagen al backend en base64.
 */
verQr(reserva: ReservaResponse) {
  this.cargandoQr.set(true);
  this.reservaService.obtenerQr(reserva.idReserva).subscribe({
    next: (data) => {
      this.cargandoQr.set(false);
      this.qrAbierto.set({ imagenBase64: data.imagenBase64, reserva });
      document.documentElement.style.overflow = 'hidden';
    },
    error: (err) => {
      this.cargandoQr.set(false);
      Swal.fire(err.error?.message || 'No se pudo cargar el código QR.');
    },
  });
}

cerrarModalQr() {
  this.qrAbierto.set(null);
  document.documentElement.style.overflow = 'auto';
}

/** Redirige a la pasarela de pago de una reserva pendiente. */
irAPagar(idReserva: number) {
  this.router.navigate(['/pago', idReserva]);
}
}