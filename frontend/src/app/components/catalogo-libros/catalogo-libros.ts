import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LibroService } from '../../services/libro.service';
import { PrestamoService } from '../../services/prestamo.service';

@Component({
  selector: 'app-catalogo-libros',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalogo-libros.html'
})
export class CatalogoLibros implements OnInit {
  listaLibros: any[] = [];
  paginaActual: number = 1;
  limitePorPagina: number = 12; 
  filtroBusqueda: string = '';
  cargando: boolean = false;


  constructor(
    private libroService: LibroService,
    private prestamoService: PrestamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarLibros();
  }

  cargarLibros() {
    this.cargando = true;
    this.libroService.listarLibros(this.paginaActual, this.limitePorPagina, this.filtroBusqueda).subscribe({
      next: (datos) => {
        this.listaLibros = datos.filter((libro: any) => libro.estado === true);
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar catálogo:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscar() {
    this.paginaActual = 1;
    this.cargarLibros();
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarLibros();
    }
  }

  solicitarLibro(libro: any) {
    if (libro.copiasDisponibles <= 0) {
      return alert('Lo sentimos, no hay copias disponibles en este momento.');
    }

    const rolUsuario = localStorage.getItem('rol');
    const idEstudianteStr = localStorage.getItem('idEstudiante');
    const idEmpleadoStr = localStorage.getItem('idEmpleado');

    if (!rolUsuario) {
      return alert('Error de sesión: No se encontró el rol. Vuelve a iniciar sesión.');
    }

    if (confirm(`¿Deseas solicitar el libro "${libro.titulo}"? Tendrás 2 días para recogerlo en biblioteca.`)) {
      
      const requestDTO: any = {
        idLibro: libro.idLibro, 
        idEstudiante: null,
        idEmpleado: null
      };

      if (rolUsuario === 'ESTUDIANTE') {
        if (!idEstudianteStr) return alert('Error: No se encontró el ID del estudiante.');
        requestDTO.idEstudiante = parseInt(idEstudianteStr, 10);
      } else if (rolUsuario === 'MAESTRO') {
        if (!idEmpleadoStr) return alert('Error: No se encontró el ID del maestro.');
        requestDTO.idEmpleado = parseInt(idEmpleadoStr, 10);
      }

      this.prestamoService.solicitarPrestamo(requestDTO).subscribe({
        next: () => {
          alert('¡Solicitud enviada exitosamente! Por favor pasa a ventanilla a recoger tu libro.');
          this.cargarLibros(); 
        },
        error: (err) => {
          alert('Error: ' + (err.error?.error || 'No se pudo realizar la solicitud.'));
        }
      });
    }
  }
}