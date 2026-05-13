import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para usar ngModel
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './faq.html',
  styleUrl: './faq.css',
})
export class Faq {

  textoBusqueda: string = '';
  categoriaActiva: string = 'todas';

  // Lista de categorías
  categorias = [
    { id: 'todas', nombre: 'Todo', icono: 'apps' },
    { id: 'general', nombre: 'General', icono: 'info'},
    { id: 'reservas', nombre: 'Reservas', icono: 'calendar_month' },
    { id: 'pagos', nombre: 'Pagos', icono: 'credit_card' },
    { id: 'instalaciones', nombre: 'Instalaciones', icono: 'stadium' },
    { id: 'cuenta', nombre: 'Mi Cuenta', icono: 'person' },
  ];

  // Lista de preguntas
  todasLasPreguntas = [
    {
      id: 1,
      categoria: 'general',
      pregunta: '¿Qué es esta plataforma y cómo funciona?',
      respuesta: 'Somos una plataforma de gestión y reservas de pistas y espacios deportivos. Puedes consultar disponibilidad y reservar la hora que mejor se adapte a ti y a tus amigos para hacer deporte juntos.',
      abierta: false
    },
    {
      id: 2,
      categoria: 'general',
      pregunta: '¿Necesito registrarme para reservar?',
      respuesta: 'Sí, es necesario crear una cuenta gratuita para poder gestionar tus reservas y acceder al recinto.',
      abierta: false
    },
    {
      id: 3,
      categoria: 'reservas',
      pregunta: '¿Cómo realizo una reserva de pista?',
      respuesta: 'Ve a la sección Polideportivos, elige el más cercano a ti o el que más te guste, mira sus pistas y elige fecha y hora. Al confirmar recibirás un código QR que te permitirá acceder a la pista.',
      abierta: false
    },
    {
      id: 4,
      categoria: 'reservas',
      pregunta: '¿Puedo cancelar una reserva ya pagada?',
      respuesta: 'Sí, puedes cancelar tu reserva hasta 24 horas antes para recibir el reembolso.',
      abierta: false
    },
    {
      id: 5,
      categoria: 'pagos',
      pregunta: '¿Qué métodos de pago aceptáis?',
      respuesta: 'Aceptamos Tarjeta de crédito/débito, Apple Pay y Bizum.',
      abierta: false
    },
    {
      id: 6,
      categoria: 'instalaciones',
      pregunta: '¿Cómo accedo a la pista el día de la reserva?',
      respuesta: 'Presenta el código QR de tu reserva en la recepción del polideportivo. ',
      abierta: false
    },
    {
      id: 7,
      categoria: 'cuenta',
      pregunta: '¿Cómo cambio mi contraseña?',
      respuesta: 'Accede a tu Perfil, luego ve a la sección de Seguridad y haz clic en Cambiar contraseña.',
      abierta: false
    }
  ];

  preguntasFiltradas = [...this.todasLasPreguntas];

  // Método para cambiar de categoría
  seleccionarCategoria(idCategoria: string) {
    this.categoriaActiva = idCategoria;
    this.filtrarPreguntas();
  }

  // Método para buscar y filtrar
  filtrarPreguntas() {
    this.preguntasFiltradas = this.todasLasPreguntas.filter(item => {
      
      // Comprobamos la categoría
      let coincideCategoria = true;
      if (this.categoriaActiva !== 'todas') {
        coincideCategoria = item.categoria === this.categoriaActiva;
      }

      // Comprobamos el texto del buscador
      let coincideTexto = true;
      if (this.textoBusqueda !== '') {
        let textoBuscadoMinusculas = this.textoBusqueda.toLowerCase();
        let preguntaMinusculas = item.pregunta.toLowerCase();
        let respuestaMinusculas = item.respuesta.toLowerCase();

        coincideTexto = preguntaMinusculas.includes(textoBuscadoMinusculas) || 
                        respuestaMinusculas.includes(textoBuscadoMinusculas);
      }

      // Tiene que cumplir las dos condiciones para mostrarse
      return coincideCategoria && coincideTexto;
    });
  }

  // Método para borrar el buscador
  limpiarBusqueda() {
    this.textoBusqueda = '';
    this.filtrarPreguntas();
  }

  // Método para abrir y cerrar la respuesta
  abrirCerrarPregunta(pregunta: any) {
    pregunta.abierta = !pregunta.abierta;
  }
}