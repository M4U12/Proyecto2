import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { InscripcionService } from '../../services/inscripcion.service';
import { EstudianteService } from '../../services/estudiante.service';
import { GradoService } from '../../services/grado.service';
import { SeccionService } from '../../services/seccion.service';
import { CicloService } from '../../services/ciclo.service';

@Component({
  selector: 'app-inscripciones',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './inscripciones.html'
})
export class Inscripciones implements OnInit {
  inscripcionForm: FormGroup;
  reasignacionForm: FormGroup;
  codigoBusqueda: string = '';
  nombreBusqueda: string = '';
  resultadosNombre: any[] = [];
  busquedaSinResultados: boolean = false;
  estudianteSeleccionado: any = null; 
  modoReasignacion: boolean = false;
  listaCiclos: any[] = [];
  listaGrados: any[] = [];
  listaSecciones: any[] = []; 

  constructor(
    private fb: FormBuilder,
    private inscripcionService: InscripcionService,
    private estudianteService: EstudianteService,
    private gradoService: GradoService,
    private seccionService: SeccionService,
    private cicloService: CicloService,
    private cdr: ChangeDetectorRef
  ) {
    this.inscripcionForm = this.fb.group({
      idCiclo: ['', Validators.required],
      idGrado: ['', Validators.required],
      idSeccion: ['', Validators.required]
    });

    this.reasignacionForm = this.fb.group({
      idNuevoGrado: ['', Validators.required],
      idNuevaSeccion: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos() {
    this.cicloService.getCiclos(1, 100).subscribe({
      next: (res) => {
        this.listaCiclos = res.filter((c: any) => c.estado === 'ABIERTO');
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar ciclos:', err)
    });

    this.gradoService.getGrados(1, 100).subscribe({
      next: (res) => {
        this.listaGrados = res;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar grados:', err)
    });
  }

  cargarSeccionesPorGrado(event: any) {
    const idGrado = event.target.value;
    this.seccionService.getSeccionesPorGrado(idGrado).subscribe({
      next: (res) => {
        this.listaSecciones = res;
        this.inscripcionForm.get('idSeccion')?.setValue(''); 
        this.reasignacionForm.get('idNuevaSeccion')?.setValue('');
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar secciones:', err)
    });
  }

  cargarSeccionesCorreccion(event: any) {
    const idGrado = event.target.value;
    this.seccionService.getSeccionesPorGrado(idGrado).subscribe({
      next: (res) => {
        this.listaSecciones = res;
        this.reasignacionForm.get('idNuevaSeccion')?.setValue(''); 
        this.cdr.detectChanges();
      }
    });
  }

  buscarPorNombre() {
    if (!this.nombreBusqueda.trim()) {
      alert('Ingresa un nombre para buscar.');
      return;
    }
    
    this.limpiarPantalla();
    
    this.estudianteService.buscarPorNombre(this.nombreBusqueda).subscribe({
      next: (datos: any[]) => {
        this.resultadosNombre = datos;
        this.busquedaSinResultados = datos.length === 0;
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('Error: ' + (err.error?.error || err.message));
        this.cdr.detectChanges();
      }
    });
  }

  seleccionarCodigo(codigo: string) {
    this.codigoBusqueda = codigo;
    this.resultadosNombre = [];
    this.nombreBusqueda = '';
    this.cdr.detectChanges();
    this.buscarExpediente();
  }

  buscarExpediente() {
    if (!this.codigoBusqueda.trim()) {
      alert('Ingresa un código personal.');
      return;
    }
    this.limpiarPantalla();

    this.estudianteService.obtenerExpediente(this.codigoBusqueda.trim()).subscribe({
      next: (datos) => {
        this.estudianteSeleccionado = datos;     
        this.modoReasignacion = this.estudianteSeleccionado.nombreGradoActual !== 'Sin asignación';
        
        if (this.modoReasignacion && this.estudianteSeleccionado.idGradoActual) {
          this.seccionService.getSeccionesPorGrado(this.estudianteSeleccionado.idGradoActual).subscribe({
            next: (res) => {
              this.listaSecciones = res;
              this.cdr.detectChanges();
            }
          });
        }
        
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('Estudiante no encontrado: ' + (err.error?.error || 'Verifica el código.'));
        this.cdr.detectChanges();
      }
    });
  }

  limpiarPantalla() {
    this.estudianteSeleccionado = null;
    this.resultadosNombre = [];
    this.busquedaSinResultados = false;
    this.modoReasignacion = false;
    this.inscripcionForm.reset();
    this.reasignacionForm.reset();
    this.listaSecciones = []; 
    this.cdr.detectChanges();
  }

  inscribir() {
    if (this.inscripcionForm.invalid) {
      alert('Selecciona ciclo, grado y sección.');
      return;
    }

    const request = {
      idEstudiante: this.estudianteSeleccionado.idEstudiante, 
      idGrado: this.inscripcionForm.value.idGrado,
      idSeccion: this.inscripcionForm.value.idSeccion,
      idCiclo: this.inscripcionForm.value.idCiclo
    };

    this.inscripcionService.inscribirEstudiante(request).subscribe({
      next: () => {
        alert('¡Estudiante inscrito exitosamente!');
        this.buscarExpediente(); 
      },
      error: (err) => {
        alert('Error: ' + (err.error?.error || 'No se pudo inscribir.'));
        this.cdr.detectChanges();
      }
    });
  }

  corregirInscripcion() {
    if (this.reasignacionForm.invalid) {
      alert('Selecciona el nuevo grado y sección.');
      return;
    }

    const idNuevoGrado = this.reasignacionForm.value.idNuevoGrado;
    const idNuevaSeccion = this.reasignacionForm.value.idNuevaSeccion;
    const idCiclo = this.estudianteSeleccionado.idCicloActivo;

    this.inscripcionService.corregirInscripcion(this.estudianteSeleccionado.idEstudiante, idCiclo, idNuevoGrado, idNuevaSeccion).subscribe({
      next: () => {
        alert('Inscripción corregida exitosamente.');
        this.buscarExpediente(); 
      },
      error: (err) => {
        alert('Error: ' + (err.error?.error || 'No se pudo corregir la matrícula.'));
        this.cdr.detectChanges();
      }
    });
  }
}