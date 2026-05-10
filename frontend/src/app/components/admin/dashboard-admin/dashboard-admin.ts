import { isPlatformBrowser } from '@angular/common';
import { ChangeDetectorRef, Component, inject, PLATFORM_ID } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminGlobalService } from '../../../services/admin-global-service';
import { crearReserva } from '../../admin-centro/crear-reserva/crear-reserva';

@Component({
  selector: 'app-dashboard-admin',
  imports: [ReactiveFormsModule, crearReserva],
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
  vistaCentroActiva: 'resumen' | 'crear-reserva' = 'resumen';

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
    metodoPagoPreferido: ['Presencial', [Validators.required]] // Valor por defecto
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
    this.cargarDirectores(0); // Al buscar, volvemos siempre a la página 0
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
          alert('Director de centro creado correctamente.');
          this.registroAdminForm.reset(); // Vaciamos el formulario
          this.registroAdminForm.get('idPolideportivo')?.setValue(''); //Volvemos a poner el select vacío por defecto
        },
        error: (err) => {
          console.error('Error del servidor:', err);
         
          const mensajeError = err.error?.mensaje || 'Hubo un error al crear el administrador.';
          alert(mensajeError);
        }
      });

    } else {
      this.registroAdminForm.markAllAsTouched(); 
    }
  }

  // Método para activar / desactivar admin centro
  cambiarEstadoDirector(idUsuario: number, estaSuspendido: boolean) {
    const accion = estaSuspendido ? 'reactivar' : 'suspender';
    // Invertimos el estado actual
    const suspender = !estaSuspendido; 
    
    if (confirm(`¿Estás seguro de que deseas ${accion} a este director?`)) {
      
      this.adminGlobalService.cambiarEstadoDirector(idUsuario, suspender).subscribe({
        next: (directorActualizado) => {
          console.log(`Estado del director cambiado con éxito:`, directorActualizado);
          
          this.cargarDirectores(); 
        },
        error: (err) => {
          console.error(`Error al intentar ${accion} al director`, err);
          alert(`Hubo un problema al intentar ${accion} al director. Revisa la consola.`);
        }
      });
      
    }
  }

  // Método para crear un polideportivo
  crearPolideportivo() {
    if (this.registroPolideportivoForm.valid) {
      
      this.adminGlobalService.crearPolideportivo(this.registroPolideportivoForm.value).subscribe({
        next: (respuesta) => {
          alert('¡Polideportivo creado con éxito!');
          
          this.registroPolideportivoForm.reset({ metodoPagoPreferido: 'Presencial' }); 
          
        },
        error: (err) => {
          console.error('Error al crear el polideportivo:', err);
          alert('Hubo un error al crear el polideportivo. Revisa la consola.');
        }
      });

    } else {
      this.registroPolideportivoForm.markAllAsTouched(); 
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
