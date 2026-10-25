import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PrestamoService } from '../../services/prestamo.service';

@Component({
  selector: 'app-mis-prestamos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-prestamos.html'
})
export class MisPrestamos implements OnInit {
  listaPrestamos: any[] = [];
  paginaActual: number = 1;
  limitePorPagina: number = 10;
  filtroEstado: string = '';
  cargando: boolean = false;

  idEstudiante: number | undefined;
  idEmpleado: number | undefined;

  constructor(
    private prestamoService: PrestamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const rol = localStorage.getItem('rol');
    
    if (rol === 'ESTUDIANTE') {
      const id = localStorage.getItem('idEstudiante');
      this.idEstudiante = id ? parseInt(id, 10) : undefined;
    } else if (rol === 'MAESTRO') {
      const id = localStorage.getItem('idEmpleado');
      this.idEmpleado = id ? parseInt(id, 10) : undefined;
    }

    this.cargarMisPrestamos();
  }

  cargarMisPrestamos() {
    this.cargando = true;
    this.prestamoService.listarPrestamos(
      this.idEstudiante, 
      this.idEmpleado, 
      this.filtroEstado, 
      this.paginaActual, 
      this.limitePorPagina
    ).subscribe({
      next: (datos) => {
        this.listaPrestamos = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar préstamos:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrarPorEstado(estado: string) {
    this.filtroEstado = estado;
    this.paginaActual = 1;
    this.cargarMisPrestamos();
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarMisPrestamos();
    }
  }
}