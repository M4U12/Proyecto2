import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PrestamoService } from '../../services/prestamo.service';

@Component({
  selector: 'app-gestion-prestamos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-prestamos.html'
})
export class GestionPrestamos implements OnInit {
  listaPrestamos: any[] = [];
  paginaActual: number = 1;
  limitePorPagina: number = 10;
  filtroEstado: string = '';
  cargando: boolean = false;
  prestamoSeleccionado: any = null;
  accionActual: 'APROBAR' | 'DEVOLVER' | 'RENOVAR' | null = null;
  inputFecha: string = '';
  inputMulta: number = 0;

  constructor(
    private prestamoService: PrestamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPrestamos();
  }

  cargarPrestamos() {
    this.cargando = true;
    this.prestamoService.listarPrestamos(undefined, undefined, this.filtroEstado, this.paginaActual, this.limitePorPagina)
      .subscribe({
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
    this.cargarPrestamos();
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarPrestamos();
    }
  }

  abrirPanel(prestamo: any, accion: 'APROBAR' | 'DEVOLVER' | 'RENOVAR') {
    this.prestamoSeleccionado = prestamo;
    this.accionActual = accion;
    
    // Valores por defecto
    const hoy = new Date();
    if (accion === 'APROBAR') {
      hoy.setDate(hoy.getDate() + 5);
      this.inputFecha = hoy.toISOString().split('T')[0];
    } else if (accion === 'RENOVAR') {
      const limiteActual = new Date(prestamo.fechaLimite);
      limiteActual.setDate(limiteActual.getDate() + 3);
      this.inputFecha = limiteActual.toISOString().split('T')[0];
    } else if (accion === 'DEVOLVER') {
      this.inputMulta = prestamo.multaGenerada || 0;
    }
  }

  cerrarPanel() {
    this.prestamoSeleccionado = null;
    this.accionActual = null;
  }

  ejecutarAccion() {
    if (!this.prestamoSeleccionado) return;
    const id = this.prestamoSeleccionado.idPrestamo;

    if (this.accionActual === 'APROBAR') {
      if (!this.inputFecha) return alert('Selecciona una fecha límite.');
      this.prestamoService.aprobarPrestamo(id, this.inputFecha).subscribe({
        next: () => this.finalizarAccion('Préstamo aprobado exitosamente.'),
        error: (err) => alert(err.error?.error || 'Error al aprobar.')
      });
    } 
    else if (this.accionActual === 'DEVOLVER') {
      if (this.inputMulta < 0) return alert('La multa no puede ser negativa.');
      this.prestamoService.devolverLibro(id, this.inputMulta).subscribe({
        next: () => this.finalizarAccion('Libro devuelto al inventario.'),
        error: (err) => alert(err.error?.error || 'Error al devolver.')
      });
    }
    else if (this.accionActual === 'RENOVAR') {
      if (!this.inputFecha) return alert('Selecciona la nueva fecha límite.');
      this.prestamoService.renovarPrestamo(id, this.inputFecha).subscribe({
        next: () => this.finalizarAccion('Préstamo renovado exitosamente.'),
        error: (err) => alert(err.error?.error || 'Error al renovar.')
      });
    }
  }

  rechazarSolicitud(idPrestamo: number) {
    if (confirm('¿Estás seguro de rechazar esta solicitud y liberar la copia?')) {
      this.prestamoService.rechazarPrestamo(idPrestamo).subscribe({
        next: () => {
          alert('Solicitud rechazada.');
          this.cerrarPanel();
          this.cargarPrestamos();
        },
        error: (err) => alert(err.error?.error || 'Error al rechazar.')
      });
    }
  }

  private finalizarAccion(mensaje: string) {
    alert(mensaje);
    this.cerrarPanel();
    this.cargarPrestamos();
  }
}