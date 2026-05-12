import { Component, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';
import { Usuario, UsuarioUpdateDTO } from '../../services/usuario';
import { FormsModule } from '@angular/forms';
import { HttpHeaders, HttpClient } from '@angular/common/http';

export interface UsuarioResponseDTO {
  idUsuario: number;
  nombre: string;
  apellidos: string;
  email: string;
  telefono: string;
  rol: string;
  puntosPenalizacion: number;
  bloqueadoHasta: string | null;
  idPolideportivoAsignado: number | null;
  fotoPerfil?: string;
}

export interface ResenaResponseDTO {
  idResena: number;
  idReserva: number;
  nombreUsuario: string;
  nombrePista: string;
  puntuacion: number;
  comentario: string;
  fecha: string;
}

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css'
})

export class Perfil implements OnInit {
  authService = inject(AuthService);
  usuarioService = inject(Usuario);
  http = inject(HttpClient);

  usuario = signal<UsuarioResponseDTO | null>(null); // Inicializamos a null mientras esperamos al backend
  resenas = signal<ResenaResponseDTO[]>([]); // Señal para las reseñas
  editando = signal<boolean>(false); // Señal que controla si estamos en modo edicion
  datosEdicion = signal<UsuarioUpdateDTO>({nombre: '', apellidos: '', telefono: '' }); // Guardamos copia temportal de los datos mientras edita
  fotoUrl = signal<string>('../../../assets/images/default-avatar.png');

  // PARA PAGINACIÓN
  paginaActual = signal<number>(0);
  pageSize = signal<number>(5);
  totalPaginas = signal<number>(0);
  totalElementos = signal<number>(0);

  getIniciales(): string {
    const user = this.usuario();
    if (!user) return '';
    const n = user.nombre?.charAt(0) || '';
    const a = user.apellidos?.charAt(0) || '';
    return `${n}${a}`.toUpperCase();
  }

  ngOnInit() {
    const token = this.authService.getToken();
    if (!token) return; 
    
    this.cargarPerfil();
    this.cargarResenas(this.paginaActual());
    this.cargarFotoPerfil();
  }

  // Carga solo los datos del usuario
  cargarPerfil() {
    this.usuarioService.obtenerMiPerfil().subscribe({
      next: (perfil) => this.usuario.set(perfil),
      error: (err) => {
        console.error('Error cargando el perfil', err);
        if (err.status === 401 || err.status === 403) {
          this.authService.logout();
        }
      }
    });
  }

  cargarResenas(page: number) {
    this.usuarioService.obtenerMisResenasPaginadas(page, this.pageSize()).subscribe({
      next: (data) => {
        this.resenas.set(data.content);
        this.paginaActual.set(page);
        this.totalPaginas.set(data.totalPages);
        this.totalElementos.set(data.totalElements);
      },
      error: (err) => console.error('Error cargando las reseñas', err)
    });
  }

  // Método para los botones de Siguiente / Anterior
  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginas()) {
      this.cargarResenas(nuevaPagina);
    }
  }

  cargarFotoPerfil() {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);

    this.http.get('http://localhost:9999/api/usuarios/mi-foto', { headers, responseType: 'blob' })
      .subscribe({
        next: (imagenBlob: Blob) => {
          console.log('2. ¡Foto descargada con éxito!', imagenBlob); // 🔥 CHIVATO 2
          const objectUrl = URL.createObjectURL(imagenBlob);
          this.fotoUrl.set(objectUrl);
        },
        error: (err: any) => {
          console.error('2. Error al descargar la foto:', err); // 🔥 CHIVATO 3
        }
      });
  }

  onCambiarFoto(event: any) {
    const archivo: File = event.target.files[0];
    
    if (archivo) {
      const formData = new FormData();
      formData.append('foto', archivo);

      const headers = new HttpHeaders().set('Authorization', `Bearer ${this.authService.getToken()}`);

      this.http.put('http://localhost:9999/api/usuarios/mi-foto', formData, { headers })
        .subscribe({
          next: () => {

            const objectUrl = URL.createObjectURL(archivo);
            this.fotoUrl.set(objectUrl);
            
            alert('¡Foto de perfil actualizada con éxito!');
          },
          error: (err: any) => {
            console.error('Error al actualizar la foto:', err);
            alert('Hubo un error al subir la nueva foto.');
          }
        });
    }
  }

  isBloqueado(): boolean {
    const user = this.usuario();
    if (!user || !user.bloqueadoHasta) return false;
    return new Date(user.bloqueadoHasta) > new Date();
  }

  activarEdicion() {
    const user = this.usuario();
    if (user) {
      this.datosEdicion.set({
        nombre: user.nombre,
        apellidos: user.apellidos,
        telefono: user.telefono
      });
      this.editando.set(true);
    }
  }

  cancelarEdicion() {
    this.editando.set(false);
  }

  guardarCambios() {
    // Llamamos al backend con los datos de los inputs
    this.usuarioService.actualizarPerfil(this.datosEdicion()).subscribe({
      next: (usuarioActualizado) => {
        // El backend nos devuelve el perfil actualizado, lo pintamos
        this.usuario.set(usuarioActualizado);
        this.editando.set(false);
      },
      error: (err) => {
        console.error('Error al actualizar el perfil', err);
        alert('Hubo un error al guardar los cambios.');
      }
    });
  }

}
