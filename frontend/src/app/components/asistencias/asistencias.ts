import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AsistenciaService } from '../../services/asistencia.service';
import { EstudianteService } from '../../services/estudiante.service';

@Component({
  selector: 'app-asistencias',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './asistencias.html'
})
export class Asistencias implements OnInit {
  idAsignacion: number = 0;
  infoClase: any = null; // para el nombre del curso y grado

  fechaSeleccionada: string = '';
  listaAlumnos: any[] = [];
  cargando: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private asistenciaService: AsistenciaService,
    private estudianteService: EstudianteService, 
    private cdr: ChangeDetectorRef
  ) {
    
    const hoy = new Date();
    hoy.setMinutes(hoy.getMinutes() - hoy.getTimezoneOffset()); 
    this.fechaSeleccionada = hoy.toISOString().split('T')[0];
    
    // datos ocultos desde el router
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
    
    this.cargarPantalla();
  }

  cambiarFecha() {
    this.cargarPantalla(); 
  }

  cargarPantalla() {
    this.cargando = true;
    this.cdr.detectChanges();

    this.estudianteService.getAlumnosPorClase(this.idAsignacion).subscribe({
      next: (alumnos) => {
        this.asistenciaService.getAsistenciaPorFecha(this.idAsignacion, this.fechaSeleccionada).subscribe({
          next: (asistencias) => {
            this.listaAlumnos = alumnos.map((alumno: any) => { //guarda los alumnos con su respectiva asistencia si tienen
              const registroPrevio = asistencias.find((a: any) => a.idEstudiante === alumno.idEstudiante); // recorre uno por uno a los estudiantes y busca en asistencia
                                                                                                          // a alumnos que tengan el mismo id
              return {
                ...alumno, //junta todo y lo guarda
                idAsistencia: registroPrevio ? registroPrevio.idAsistencia : null,
                estadoAsistencia: registroPrevio ? registroPrevio.estado : 'SIN REGISTRO'
              };
            });

            this.cargando = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.cargando = false;
            this.cdr.detectChanges();
          }
        });
      },
      error: (err) => {
        alert('Error al cargar la lista de alumnos: ' + (err.error?.error || err.message));
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  marcarAsistencia(alumno: any, estado: string) {
    // si ya existe un idAsistencia, se hace un update
    if (alumno.idAsistencia) {
      this.asistenciaService.modificarAsistencia(alumno.idAsistencia, estado).subscribe({
        next: () => {
          alumno.estadoAsistencia = estado; 
          this.cdr.detectChanges();
        },
        error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
      });
    } 
    // Si es sin registro, se crea
    else {
      const inf = {
        idEstudiante: alumno.idEstudiante,
        idAsignacion: this.idAsignacion,
        fecha: this.fechaSeleccionada,
        estado: estado // PRESENTE, AUSENTE o JUSTIFICADO 
      };

      this.asistenciaService.registrarAsistencia(inf).subscribe({
        next: () => {
          this.cargarPantalla(); 
        },
        error: (err) => alert('Error al registrar: ' + (err.error?.error || 'Intenta de nuevo.'))
      });
    }
  }

  volver() {
    this.router.navigate(['/dashboard/control-maestro']);
  }
}