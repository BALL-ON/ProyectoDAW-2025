import { isPlatformBrowser, NgClass } from '@angular/common';
import { ChangeDetectorRef, Component, inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminGlobalService } from '../../../services/admin-global-service';
import { TipoPistaService } from '../../../services/tipo-pista-service';
import { ListaReservas } from '../../admin-centro/lista-reservas/lista-reservas';
import { GestionPistas } from '../../admin-centro/gestion-pistas/gestion-pistas';
import { GestionResenas } from '../../admin-centro/gestion-resenas/gestion-resenas';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dashboard-admin',
  imports: [ReactiveFormsModule, NgClass, ListaReservas, GestionPistas, GestionResenas],
  templateUrl: './dashboard-admin.html',
  styleUrl: './dashboard-admin.css',
})
export class DashboardAdmin {

  rolUsuario: string | null = null;
  private platformId = inject(PLATFORM_ID);
  private fb = inject(FormBuilder);
  directores: any[] = [];
  polideportivos: any[] = [];
  cargando: boolean = true;
  cargandoPoli: boolean = true;
  private cdRef = inject(ChangeDetectorRef);
  private adminGlobalService = inject(AdminGlobalService);
  private tipoPistaService = inject(TipoPistaService);
  vistaCentroActiva: 'resumen' | 'crear-reserva' | 'ver-reservas' | 'gestion-pistas' | 'gestion-resenas' = 'resumen';

  paginaActual: number = 0;
  tamanoPagina: number = 5;
  totalPaginas: number = 0;
  totalElementos: number = 0;

  // Buscador reactivo
  filtrosDirectores = this.fb.group({
    nombre: [''],
    email: ['']
  });

  paginaActualPoli: number = 0;
  tamanoPaginaPoli: number = 5;
  totalPaginasPoli: number = 0;
  totalElementosPoli: number = 0;

  // Buscador reactivo para Polideportivos
  filtrosPoli = this.fb.group({
    nombre: [''],
    poblacion: ['']
  });

  // formulario reactivo crear admin_centro
  registroAdminForm: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    apellidos: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    contrasena: ['', [Validators.required, Validators.minLength(6), Validators.pattern(/^(?=.*[A-Z])(?=.*[\W_]).{6,}$/)]],
    telefono: ['', [Validators.pattern(/^[0-9]{9}$/)]],
    idPolideportivo: ['', [Validators.required]]
  });

  // formulario reactivo crear polideportivo
  registroPolideportivoForm: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    direccion: ['', [Validators.required]],
    poblacion: ['', [Validators.required]],
    metodoPagoPreferido: ['Presencial', [Validators.required]]
  });

  // formulario reactivo crear tipo de pista
  registroTipoPistaForm: FormGroup = this.fb.group({
    nombreTipo: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: ['']
  });

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.rolUsuario = sessionStorage.getItem('user_rol');

      this.cargarDirectores();
      this.cargarPolideportivos();
    }
  }

  cargarDirectores(pagina: number = 0) {
    this.cargando = true;

    const nombreFiltro = this.filtrosDirectores.value.nombre || undefined;
    const emailFiltro = this.filtrosDirectores.value.email || undefined;

    this.adminGlobalService.obtenerDirectores(pagina, this.tamanoPagina, nombreFiltro, emailFiltro).subscribe({
      next: (respuesta) => {
        this.paginaActual = pagina;
        
        this.directores = respuesta.content ? [...respuesta.content] : [];
        
        this.totalPaginas = respuesta.totalPages || 0;
        this.totalElementos = respuesta.totalElements || 0;
        
        this.cargando = false; 
        
        this.cdRef.markForCheck(); 
      },
      error: (err) => {
        console.error('Error al cargar la página', pagina, err);
        this.cargando = false;
        this.cdRef.markForCheck();
      }
    });
  }

  // Métodos extra para los botones del HTML
  buscarConFiltros() {
    this.cargarDirectores(0);
  }

  limpiarFiltros() {
    this.filtrosDirectores.reset({ nombre: '', email: '' });
    this.cargarDirectores(0);
  }

  cambiarPagina(direccion: number) {
    if (this.cargando) return;

    const nuevaPagina = this.paginaActual + direccion;
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginas) {
      this.cargarDirectores(nuevaPagina);
    }
  }

  // Método para crear un admin centro
  crearAdminCentro() {
    if (this.registroAdminForm.valid) {
      
      this.adminGlobalService.registrarDirector(this.registroAdminForm.value).subscribe({
        next: (respuesta) => {
          Swal.fire('Director de centro creado correctamente.');
          this.registroAdminForm.reset();
          this.registroAdminForm.get('idPolideportivo')?.setValue('');
          this.cargarDirectores();
        },
        error: (err) => {
          console.error('Error del servidor:', err);
         
          const mensajeError = err.error?.mensaje || 'Hubo un error al crear el administrador.';
          Swal.fire(mensajeError);
        }
      });

    } else {
      this.registroAdminForm.markAllAsTouched(); 
    }

    
  }

  // Método para activar / desactivar admin centro
  cambiarEstadoDirector(idUsuario: number, estaSuspendido: boolean) {
    const accion = estaSuspendido ? 'reactivar' : 'suspender';
    const suspender = !estaSuspendido; 
    
    if (confirm(`¿Estás seguro de que deseas ${accion} a este director?`)) {
      
      this.adminGlobalService.cambiarEstadoDirector(idUsuario, suspender).subscribe({
        next: (directorActualizado) => {
          console.log(`Estado del director cambiado con éxito:`, directorActualizado);
          
          this.cargarDirectores(); 
        },
        error: (err) => {
          console.error(`Error al intentar ${accion} al director`, err);
          Swal.fire(`Hubo un problema al intentar ${accion} al director. Revisa la consola.`);
        }
      });
      
    }
  }

  // Método para crear un polideportivo
  crearPolideportivo() {
    if (this.registroPolideportivoForm.valid) {
      
      this.adminGlobalService.crearPolideportivo(this.registroPolideportivoForm.value).subscribe({
        next: (respuesta) => {
          Swal.fire('¡Polideportivo creado con éxito!');
          this.registroPolideportivoForm.reset({ metodoPagoPreferido: 'Presencial' }); 
          this.cargarPolideportivos();
        },
        error: (err) => {
          console.error('Error al crear el polideportivo:', err);
          Swal.fire('Hubo un error al crear el polideportivo. Revisa la consola.');
        }
      });

    } else {
      this.registroPolideportivoForm.markAllAsTouched(); 
    }
  }

  // Método para crear un tipo de pista
  crearTipoPista() {
    if (this.registroTipoPistaForm.valid) {
      this.tipoPistaService.crear(this.registroTipoPistaForm.value).subscribe({
        next: () => {
          Swal.fire('¡Tipo de pista creado con éxito!');
          this.registroTipoPistaForm.reset();
        },
        error: (err) => {
          console.error('Error al crear el tipo de pista:', err);
          const mensajeError = err.error?.mensaje || 'Hubo un error al crear el tipo de pista.';
          Swal.fire(mensajeError);
        }
      });
    } else {
      this.registroTipoPistaForm.markAllAsTouched();
    }
  }

  cargarPolideportivos(pagina: number = 0) {
    this.cargandoPoli = true;

    const nombreFiltro = this.filtrosPoli.value.nombre || undefined;
    const poblacionFiltro = this.filtrosPoli.value.poblacion || undefined;

    this.adminGlobalService.obtenerPolideportivos(pagina, this.tamanoPaginaPoli, nombreFiltro, poblacionFiltro).subscribe({
      next: (respuesta) => {
        this.paginaActualPoli = pagina;
        this.polideportivos = respuesta.content ? [...respuesta.content] : (Array.isArray(respuesta) ? respuesta : []);
        this.totalPaginasPoli = respuesta.totalPages || 0;
        this.totalElementosPoli = respuesta.totalElements || 0;
        
        this.cargandoPoli = false;
        this.cdRef.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar polideportivos', err);
        this.cargandoPoli = false;
        this.cdRef.markForCheck();
      }
    });
  }

  cambiarPaginaPoli(direccion: number) {
    if (this.cargandoPoli) return;
    const nuevaPagina = this.paginaActualPoli + direccion;
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginasPoli) {
      this.cargarPolideportivos(nuevaPagina);
    }
  }

  buscarPoliConFiltros() {
    this.cargarPolideportivos(0);
  }

  limpiarFiltrosPoli() {
    this.filtrosPoli.reset({ nombre: '', poblacion: '' });
    this.cargarPolideportivos(0);
  }

}
