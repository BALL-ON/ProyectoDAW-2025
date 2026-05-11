import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResenaService } from '../../../services/ResenaService';

@Component({
  selector: 'app-gestion-resenas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gestion-resenas.html',
  styleUrl: './gestion-resenas.css'
})
export class GestionResenas implements OnInit {
  resenas: any[] = [];
  cargando: boolean = true;

  // Array para poder pintar las 5 estrellas en el HTML con un bucle
  estrellas = [1, 2, 3, 4, 5]; 

  constructor(private resenaService: ResenaService) {}

  ngOnInit(): void {
    this.cargarResenas();
  }

  cargarResenas() {
    const idStr = sessionStorage.getItem('idPolideportivo');
    if (!idStr) {
      this.cargando = false;
      return;
    }

    this.resenaService.obtenerPorPolideportivo(Number(idStr)).subscribe({
      next: (data) => {
        this.resenas = data;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar reseñas', err);
        this.cargando = false;
      }
    });
  }

  toggleVisibilidad(resena: any) {
    const nuevaVisibilidad = !resena.visible;
    resena.procesando = true;

    this.resenaService.cambiarVisibilidad(resena.idResena, nuevaVisibilidad).subscribe({
      next: () => {
        resena.visible = nuevaVisibilidad;
        resena.procesando = false;
      },
      error: () => {
        alert('Error al cambiar la visibilidad de la reseña.');
        resena.procesando = false;
      }
    });
  }
}
