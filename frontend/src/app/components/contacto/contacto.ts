import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contacto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contacto.html',
  styleUrl: './contacto.css',
})
export class Contacto {

  enviando = false;
  enviado  = false;

  form = {
    nombre:  '',
    email:   '',
    asunto:  'reserva',
    mensaje: ''
  };

  enviarMensaje() {
    this.enviando = true;
    // Aquí harías tu llamada al servicio/API
    setTimeout(() => {
      this.enviando = false;
      this.enviado  = true;
      this.form = { nombre: '', email: '', asunto: 'reserva', mensaje: '' };
    }, 1000);
  }
}