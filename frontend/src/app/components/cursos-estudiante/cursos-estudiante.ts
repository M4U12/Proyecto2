import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { EstudianteService } from '../../services/estudiante.service';

@Component({
  selector: 'app-cursos-estudiante',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cursos-estudiante.html'
})
export class CursosEstudiante implements OnInit {
  idEstudiante: number = 0;
  listaCursos: any[] = [];
  cargando: boolean = false;

  constructor(
    private router: Router,
    private estudianteService: EstudianteService,
    private cdr: ChangeDetectorRef
  ) {
    const usuarioString = localStorage.getItem('usuarioLogueado'); 
    if (usuarioString) {
      const usuario = JSON.parse(usuarioString);
      this.idEstudiante = usuario.idEstudiante; 
    }
  }

  ngOnInit(): void {
    if (!this.idEstudiante) {
      alert('Error de sesión. Por favor, inicie sesión nuevamente.');
      this.router.navigate(['/login']);
      return;
    }
    this.cargarMisCursos();
  }

  cargarMisCursos() {
    this.cargando = true;
    this.cdr.detectChanges();

    this.estudianteService.getCursosPorEstudiante(this.idEstudiante).subscribe({
      next: (cursos) => {
        this.listaCursos = cursos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('No se pudieron cargar sus cursos.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  irABoleta(idAsignacion: number, nombreCurso: string) {
    this.router.navigate(['/dashboard/boleta'], {
      state: { 
        idAsig: idAsignacion, 
        curso: nombreCurso 
      }
    });
  }
}