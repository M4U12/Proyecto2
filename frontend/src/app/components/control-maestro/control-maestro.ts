import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AsignacionService } from '../../services/asignacion.service';
import { AuthService } from '../../services/auth';
import { CicloService } from '../../services/ciclo.service';

@Component({
  selector: 'app-control-maestro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './control-maestro.html'
})
export class ControlMaestro implements OnInit {
  misAsignaciones: any[] = [];
  cicloActivo: any = null;
  cargando: boolean = true;
  idMaestro: number | null = null;

  constructor(
    private asignacionService: AsignacionService,
    private authService: AuthService,
    private cicloService: CicloService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.idMaestro = this.authService.obtenerIdEmpleado();

    if (!this.idMaestro) {
      alert('Error: No se pudo identificar al maestro. Por favor inicie sesión nuevamente.');
      this.router.navigate(['/login']);
      return;
    }

    this.cargarCicloYAsignaciones();
  }

  cargarCicloYAsignaciones() {
    // se busca el ciclo escolar abierto actualmente
    this.cicloService.getCiclos(1, 10).subscribe({
      next: (res) => {
        const ciclosAbiertos = res.filter((c: any) => c.estado === 'ABIERTO' || c.estado === true);
        
        if (ciclosAbiertos.length > 0) {
          this.cicloActivo = ciclosAbiertos[0];
          
          this.asignacionService.listarAsignacionesPorMaestro(this.idMaestro!, this.cicloActivo.idCiclo).subscribe({
            next: (asignaciones) => {
              this.misAsignaciones = asignaciones;
              this.cargando = false;
              this.cdr.detectChanges();
            },
            error: (err) => {
              console.error('Error al cargar clases', err);
              this.cargando = false;
              this.cdr.detectChanges();
            }
          });

        } else {
          this.cargando = false;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error('Error al cargar el ciclo', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

 
  irAAsistencia(idAsignacion: number, nombreCurso: string, gradoSeccion: string) {
    this.router.navigate(['/dashboard/asistencia'], {
      state: { 
        idAsig: idAsignacion, // viaja oculto 
        curso: nombreCurso, 
        descripcion: gradoSeccion 
      }
    });
  }

  irANotas(idAsignacion: number, nombreCurso: string, gradoSeccion: string) {
    this.router.navigate(['/dashboard/notas'], {
      state: { idAsig: idAsignacion, curso: nombreCurso, descripcion: gradoSeccion }
    });
  }
}