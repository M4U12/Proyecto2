import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NotaService } from '../../services/nota.service'; 

@Component({
  selector: 'app-boleta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './boleta.html'
})
export class Boleta implements OnInit {
  infoClase: any = null;
  idAsignacion: number = 0;
  idEstudiante: number = 0;

  boletaNotas: any[] = [];
  zonaTotal: number = 0;
  cargando: boolean = false;

  constructor(
    private router: Router,
    private notaService: NotaService,
    private cdr: ChangeDetectorRef
  ) {
    // extrae el id del estudiante de la memoria
    const usuarioString = localStorage.getItem('usuarioLogueado'); 
    if (usuarioString) {
      const usuario = JSON.parse(usuarioString);
      this.idEstudiante = usuario.idEstudiante; 
    }

    // extraemos el id de la clase desde la navegación
    const nav = this.router.getCurrentNavigation();
    if (nav?.extras.state) {
      this.infoClase = nav.extras.state;
      this.idAsignacion = this.infoClase.idAsig;
    }
  }

  ngOnInit(): void {
    if (!this.idAsignacion || !this.idEstudiante) {
      this.volver();
      return;
    }
    this.cargarBoleta();
  }

  cargarBoleta() {
    this.cargando = true;
    this.cdr.detectChanges();

    this.notaService.obtenerBoleta(this.idEstudiante, this.idAsignacion).subscribe({
      next: (datos) => {
        this.boletaNotas = datos;
        this.calcularTotal();
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        alert('Error al cargar la boleta de calificaciones.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  calcularTotal() {
    // Suma las notas, si el profe no ha calificado (null), lo toma como 0 para la suma temporal
    this.zonaTotal = this.boletaNotas.reduce((acumulador, actividad) => {
      return acumulador + (actividad.punteoObtenido !== null ? actividad.punteoObtenido : 0);
    }, 0);
  }

  volver() {
    this.router.navigate(['/dashboard/cursos-estudiante']);
  }
}