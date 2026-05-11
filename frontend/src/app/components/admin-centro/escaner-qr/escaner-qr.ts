import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZXingScannerModule } from '@zxing/ngx-scanner';
import { AdminCentroService } from '../../../services/admin-centro-service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-escaner-qr',
  standalone: true,
  imports: [CommonModule, ZXingScannerModule, RouterLink],
  templateUrl: './escaner-qr.html',
  styleUrl: './escaner-qr.css'
})
export class EscanerQr implements OnInit {
  camaras: MediaDeviceInfo[] = [];
  camaraSeleccionada: MediaDeviceInfo | undefined;
  
  // Variables de control de estado
  resultadoEscaneo: string = '';
  estadoValidacion: 'esperando' | 'valida' | 'invalida' = 'esperando';
  datosReserva: any = null;
  mensajeError: string = '';
  procesando: boolean = false;

  constructor(private adminCentroService: AdminCentroService) {}

  ngOnInit(): void {}

  alEncontrarCamaras(camaras: MediaDeviceInfo[]) {
    this.camaras = camaras;
    if (camaras && camaras.length > 0) {
      const camaraTrasera = camaras.find(c => c.label.toLowerCase().includes('back'));
      this.camaraSeleccionada = camaraTrasera ? camaraTrasera : camaras[0];
    }
  }

  alEscanearExito(resultado: string) {
    if (this.procesando) return;

    this.procesando = true;
    this.resultadoEscaneo = resultado;
    
    // Llamamos a nuestro nuevo endpoint
    this.adminCentroService.validarReservaQr(resultado).subscribe({
      next: (respuesta) => {
        this.estadoValidacion = 'valida';
        this.datosReserva = respuesta;
        
        setTimeout(() => this.resetearEscaner(), 4000);
      },
      error: (err) => {
        this.estadoValidacion = 'invalida';
        this.mensajeError = err.error || 'Código QR no válido';

        setTimeout(() => this.resetearEscaner(), 4000);
      }
    });
  }

  resetearEscaner() {
    this.resultadoEscaneo = '';
    this.estadoValidacion = 'esperando';
    this.datosReserva = null;
    this.mensajeError = '';
    this.procesando = false;
  }
}
