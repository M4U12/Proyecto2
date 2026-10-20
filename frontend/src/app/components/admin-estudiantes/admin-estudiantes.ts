import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EstudianteService } from '../../services/estudiante.service';

@Component({
  selector: 'app-admin-estudiantes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-estudiantes.html'
})
export class AdminEstudiantes implements OnInit {
  listaEstudiantes: any[] = [];
  paginaActual: number = 1;
  limitePorPagina: number = 10;
  filtroBusqueda: string = '';
  cargando: boolean = false;

  constructor(
    private estudianteService: EstudianteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarEstudiantes();
  }

  cargarEstudiantes() {
    this.cargando = true;
    this.estudianteService.getEstudiantesPaginados(this.paginaActual, this.limitePorPagina, this.filtroBusqueda).subscribe({
      next: (datos) => {
        this.listaEstudiantes = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar alumnos:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscar() {
    this.paginaActual = 1; 
    this.cargarEstudiantes();
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarEstudiantes();
    }
  }

  cambiarEstado(codigo: string, nuevoEstado: string) {
    if (confirm(`¿Estás seguro de cambiar el estado de este alumno a ${nuevoEstado}?`)) {
      this.estudianteService.cambiarEstado(codigo, nuevoEstado).subscribe({
        next: () => {
          alert(`Estado actualizado a ${nuevoEstado}`);
          this.cargarEstudiantes(); 
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo cambiar el estado.'))
      });
    }
  }

  generarCredenciales(estudiante: any) {
    const primerNombre = estudiante.nombres.split(' ')[0].toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    const primerApellido = estudiante.apellidos.split(' ')[0].toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    const partesCodigo = estudiante.codigoPersonal.split('-');
    const correlativo = partesCodigo[partesCodigo.length - 1]; 

    const correoGenerado = `${primerNombre}.${primerApellido}.${correlativo}@colegio.edu.gt`;
    const passwordGenerada = estudiante.codigoPersonal;

    const mensaje = `Se crearán credenciales de acceso para el alumno:\n\n` +
                    ` Correo: ${correoGenerado}\n` +
                    ` Contraseña: ${passwordGenerada}\n\n` +
                    `¿Desea continuar?`;

    if (confirm(mensaje)) {
      const inf = {
        idEstudiante: estudiante.idEstudiante, 
        correo: correoGenerado,
        password: passwordGenerada
      };

      this.estudianteService.crearCredenciales(inf).subscribe({
        next: () => alert('Credenciales generadas exitosamente.'),
        error: (err) => alert('Error: ' + (err.error?.error || 'El estudiante ya tiene cuenta o hubo un error.'))
      });
    }
  }
}