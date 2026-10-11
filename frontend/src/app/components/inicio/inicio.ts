import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CarreraService } from '../../services/carrera.service';
import { NivelService } from '../../services/nivel.service';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './inicio.html'
})
export class Inicio implements OnInit {
  carrerasActivas: any[] = [];
  nivelesActivos: any[] = [];
  
  cargando: boolean = true;
  mostrarBotonLogin: boolean = true;
  private peticionesCompletadas: number = 0;

  constructor(
    private carreraService: CarreraService,
    private nivelService: NivelService,
    private router: Router,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() {
    if (this.router.url.includes('/dashboard')) {
      this.mostrarBotonLogin = false;
    }
    this.cargarDatos();
  }

  cargarDatos() {
    this.carreraService.getCarreras(1, 100).subscribe({
      next: (datos) => {
        const arreglo = Array.isArray(datos) ? datos : [];
        this.carrerasActivas = arreglo.filter(c => c.estado === true);
        this.verificarCarga();
      },
      error: (err) => {
        console.error('Error al cargar carreras', err);
        this.verificarCarga();
      }
    });

    this.nivelService.getNiveles().subscribe({
      next: (datos) => {
        const arreglo = Array.isArray(datos) ? datos : [];
        this.nivelesActivos = arreglo.filter(n => n.estado === true);
        this.verificarCarga();
      },
      error: (err) => {
        console.error('Error al cargar niveles', err);
        this.verificarCarga();
      }
    });
  }

  verificarCarga() {
    this.peticionesCompletadas++;
    if (this.peticionesCompletadas >= 2) {
      this.cargando = false;
      this.cdr.detectChanges(); 
    }
  }
}