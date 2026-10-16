import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AsignacionService } from '../../services/asignacion.service';
import { EmpleadoService } from '../../services/empleado.service';
import { CicloService } from '../../services/ciclo.service';
import { GradoService } from '../../services/grado.service';
import { SeccionService } from '../../services/seccion.service';
import { CursoService } from '../../services/curso.service';

@Component({
  selector: 'app-asignaciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './asignaciones.html'
})
export class Asignaciones implements OnInit {
  asignacionForm: FormGroup;
  
  listaCiclos: any[] = [];
  listaMaestros: any[] = [];
  listaCursos: any[] = [];
  listaGrados: any[] = [];
  listaSecciones: any[] = [];
  
  asignacionesActuales: any[] = [];
  maestroSeleccionado: any = null;
  cicloSeleccionadoId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private asignacionService: AsignacionService,
    private empleadoService: EmpleadoService,
    private cicloService: CicloService,
    private gradoService: GradoService,
    private seccionService: SeccionService,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef
  ) {
    this.asignacionForm = this.fb.group({
      idCurso: ['', Validators.required],
      idGrado: ['', Validators.required],
      idSeccion: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos() {
    // Ciclos activos
    this.cicloService.getCiclos(1, 100).subscribe(res => {
      this.listaCiclos = res.filter((c: any) => c.estado === 'ABIERTO' || c.estado === true);
      this.cdr.detectChanges();
    });

    // Filtra solo a los MAESTROS de la nómina
    this.empleadoService.getEmpleados(1, 100).subscribe(res => {
      // Ajusta 'puesto' o 'rolSistema' según cómo los identifiques en tu BD
      this.listaMaestros = res.filter((e: any) => e.rolSistema === 'MAESTRO');
      this.cdr.detectChanges();
    });

    // Grados
    this.gradoService.getGrados(1, 100).subscribe(res => {
      this.listaGrados = res;
      this.cdr.detectChanges();
    });

    // Cursos
    this.cursoService.getCursos(1, 100).subscribe(res => this.listaCursos = res);
  }

  cargarSecciones(event: any) {
    const idGrado = event.target.value;
    this.seccionService.getSeccionesPorGrado(idGrado).subscribe({
      next: (res) => {
        this.listaSecciones = res;
        this.asignacionForm.get('idSeccion')?.setValue('');
        this.cdr.detectChanges();
      }
    });
  }

  seleccionarContexto(event: any, tipo: 'maestro' | 'ciclo') {
    if (tipo === 'maestro') {
      const idStr = event.target.value;
      this.maestroSeleccionado = this.listaMaestros.find(m => m.idEmpleado.toString() === idStr);
    } else {
      this.cicloSeleccionadoId = Number(event.target.value);
    }

    if (this.maestroSeleccionado && this.cicloSeleccionadoId) {
      this.cargarAsignaciones();
    }
  }

  cargarAsignaciones() {
    this.asignacionService.listarAsignacionesPorMaestro(this.maestroSeleccionado.idEmpleado, this.cicloSeleccionadoId!)
      .subscribe({
        next: (datos) => {
          this.asignacionesActuales = datos;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error al cargar asignaciones', err)
      });
  }

  asignarCurso() {
    if (this.asignacionForm.invalid || !this.maestroSeleccionado || !this.cicloSeleccionadoId) {
      alert('Seleccione maestro, ciclo, curso, grado y sección.');
      return;
    }

    const request = {
      idEmpleado: this.maestroSeleccionado.idEmpleado,
      idCiclo: this.cicloSeleccionadoId,
      idCurso: this.asignacionForm.value.idCurso,
      idSeccion: this.asignacionForm.value.idSeccion
    };

    this.asignacionService.registrarAsignacion(request).subscribe({
      next: () => {
        alert('Curso asignado al maestro exitosamente.');
        this.asignacionForm.reset();
        this.listaSecciones = [];
        this.cargarAsignaciones(); 
      },
      error: (err) => {
        alert('Error: ' + (err.error?.error || 'No se pudo asignar el curso. ¿Ya está asignado?'));
        this.cdr.detectChanges();
      }
    });
  }

  desasignarCurso(idAsignacion: number) {
    if (confirm('¿Estás seguro de quitar este curso de la carga del maestro?')) {
      this.asignacionService.eliminarAsignacion(idAsignacion).subscribe({
        next: () => {
          alert('Curso desasignado exitosamente.');
          this.cargarAsignaciones(); 
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo eliminar.'))
      });
    }
  }



  asignacionAReasignar: any = null;
  idNuevoMaestroTraspaso: string = '';
  abrirModalReasignacion(asig: any) {
    this.asignacionAReasignar = asig;
    this.idNuevoMaestroTraspaso = '';
  }
  efectuarTraspaso() {
    if (!this.idNuevoMaestroTraspaso) {
      alert('Selecciona un nuevo maestro.');
      return;
    }

    this.asignacionService.reasignarTitular(this.asignacionAReasignar.idAsignacion, Number(this.idNuevoMaestroTraspaso))
      .subscribe({
        next: () => {
          alert('El curso ha sido transferido al nuevo maestro.');
          this.asignacionesActuales = []; 
          this.maestroSeleccionado = null; 
          this.cicloSeleccionadoId = null; 
          this.asignacionAReasignar = null;
          this.asignacionForm.reset();
          this.listaSecciones = [];
          
          this.cdr.detectChanges();
        },
        error: (err) => {alert('Error: ' + (err.error?.error || 'No se pudo hacer el traspaso.'))
        this.cdr.detectChanges();
        }
      });
  }
}