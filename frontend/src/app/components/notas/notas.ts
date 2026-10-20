import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EstudianteService } from '../../services/estudiante.service';
import { ActividadService } from '../../services/actividad.service';
import { NotaService } from '../../services/nota.service';

@Component({
  selector: 'app-notas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './notas.html'
})
export class Notas implements OnInit {
  idAsignacion: number = 0;
  infoClase: any = null;
  listaAlumnos: any[] = [];
  listaActividades: any[] = [];
  cargando: boolean = false;
  actividadForm: FormGroup;
  actividadEditando: any = null;

  constructor(
    private router: Router,
    private fb: FormBuilder,
    private estudianteService: EstudianteService,
    private actividadService: ActividadService,
    private notaService: NotaService,
    private cdr: ChangeDetectorRef
  ) {
    this.actividadForm = this.fb.group({
      nombre: ['', Validators.required],
      descripcion: [''],
      ponderacion: ['', [Validators.required, Validators.min(0.1)]]
    });

    const nav = this.router.getCurrentNavigation();
    if (nav?.extras.state) {
      this.infoClase = nav.extras.state;
      this.idAsignacion = this.infoClase.idAsig;
    }
  }

  ngOnInit(): void {
    if (!this.idAsignacion) {
      this.volver();
      return;
    }
    this.cargarCuadroDeNotas();
  }

  cargarCuadroDeNotas() {
    this.cargando = true;
    this.cdr.detectChanges();

    // cargar estudiantes
    this.estudianteService.getAlumnosPorClase(this.idAsignacion).subscribe({
      next: (alumnos) => {
        this.listaAlumnos = alumnos.map((al: any) => ({ ...al, notas: {}, zonaTotal: 0 }));
        this.actividadService.listarActividades(this.idAsignacion).subscribe({
          next: (actividades) => {
            this.listaActividades = actividades;

            if (this.listaActividades.length === 0) {
              this.cargando = false;
              this.cdr.detectChanges();
              return;
            }

            let peticionesCompletadas = 0;
            this.listaActividades.forEach(act => {
              this.notaService.listarNotasPorActividad(act.idActividad).subscribe({
                next: (notas) => {
                  notas.forEach((nota: any) => {
                    const alumno = this.listaAlumnos.find(a => a.idEstudiante === nota.idEstudiante);
                    if (alumno) {
                      alumno.notas[act.idActividad] = {
                        idNota: nota.idNota,
                        punteo: nota.punteoObtenido
                      };
                    }
                  });

                  peticionesCompletadas++;
                  if (peticionesCompletadas === this.listaActividades.length) {
                    this.calcularZonasGlobales();
                    this.cargando = false;
                    this.cdr.detectChanges();
                  }
                }
              });
            });
          }
        });
      },
      error: () => {
        alert('Error al cargar la clase.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  calcularZonasGlobales() {
    this.listaAlumnos.forEach(alumno => {
      let suma = 0;
      for (const key in alumno.notas) {
        suma += alumno.notas[key].punteo;
      }
      alumno.zonaTotal = suma;
    });
  }

  abrirModalActividad(actividad: any = null) {
    this.actividadEditando = actividad;
    if (actividad) {
      this.actividadForm.patchValue(actividad);
    } else {
      this.actividadForm.reset();
    }
  }

  guardarActividad() {
    if (this.actividadForm.invalid) return;

    // se extrae el valor que el maestro escribió en el formulario
    const ponderacionNueva = parseFloat(this.actividadForm.value.ponderacion);

    // calculo de punteo
    let sumaExistente = 0;
    this.listaActividades.forEach(act => {
      // si está editando una actividad, no suma su valor antiguo, para no duplicarlo
      if (this.actividadEditando && act.idActividad === this.actividadEditando.idActividad) {
        return; 
      }
      sumaExistente += parseFloat(act.ponderacion);
    });

    // si lo que ya existe más lo nuevo supera 100, se detiene todo
    if ((sumaExistente + ponderacionNueva) > 100) {
      const disponibles = 100 - sumaExistente;
      alert(`Límite superado\nActualmente tienes ${sumaExistente} puntos distribuidos.\nSolo tienes ${disponibles} puntos disponibles para esta clase.`);
      return; 
    }

    // no pasa de 100
    const datos = {
      idAsignacion: this.idAsignacion,
      ...this.actividadForm.value
    };

    if (this.actividadEditando) {
      this.actividadService.modificarActividad(this.actividadEditando.idActividad, datos).subscribe({
        next: () => {
          this.cargarCuadroDeNotas(); 
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo modificar.'))
      });
    } else {
      this.actividadService.crearActividad(datos).subscribe({
        next: () => {
          this.cargarCuadroDeNotas();
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo crear.'))
      });
    }
  }


  guardarNota(alumno: any, actividad: any, event: any) {
    let valorInput = event.target.value;
    const celdaActual = alumno.notas[actividad.idActividad];

    // si el maestro dejó vacío
    if (valorInput.trim() === '') {
      if (!celdaActual) return; // no había notas y no hizo nada
      valorInput = '0'; // si borró una nota se queda en 0
    }

    const valorIngresado = parseFloat(valorInput);
    if (celdaActual && celdaActual.punteo === valorIngresado) {
      return; 
    }

    if (valorIngresado > actividad.ponderacion) {
      alert(`El punteo máximo para esta actividad es ${actividad.ponderacion} puntos.`);
      event.target.value = celdaActual?.punteo !== undefined ? celdaActual.punteo : '';
      return;
    }

    if (valorIngresado < 0) {
      alert('El punteo no puede ser negativo.');
      event.target.value = celdaActual?.punteo !== undefined ? celdaActual.punteo : '';
      return;
    }

    if (celdaActual) {
      this.notaService.modificarNota(celdaActual.idNota, valorIngresado).subscribe({
        next: () => {
          celdaActual.punteo = valorIngresado;
          this.calcularZonasGlobales(); 
          this.cdr.detectChanges();
        }
      });
    } else {
      const payload = {
        idActividad: actividad.idActividad,
        idEstudiante: alumno.idEstudiante,
        punteoObtenido: valorIngresado
      };

      this.notaService.registrarNota(payload).subscribe({
        next: () => {
          this.cargarCuadroDeNotas();
        }
      });
    }
  }

  volver() {
    this.router.navigate(['/dashboard/control-maestro']);
  }
}