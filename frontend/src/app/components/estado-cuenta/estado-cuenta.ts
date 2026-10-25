import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PagoService } from '../../services/pago.service';
import { CicloService } from '../../services/ciclo.service';

@Component({
  selector: 'app-estado-cuenta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './estado-cuenta.html'
})
export class EstadoCuenta implements OnInit {
  estadoCuenta: any = null;
  idCicloActivo: number = 0;
  idEstudianteLogueado: number = 0;

  constructor(
    private pagoService: PagoService,
    private cicloService: CicloService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const idGuardado = localStorage.getItem('idEstudiante');
    if (idGuardado) {
      this.idEstudianteLogueado = parseInt(idGuardado, 10);
      this.cargarCicloActivo();
    } else {
      alert("Error: No se encontró la sesión del estudiante.");
    }
  }

  cargarCicloActivo() {
    this.cicloService.getCiclos(1, 10).subscribe({
      next: (res: any) => {
        const ciclo = res.find((c: any) => c.estado === 'ABIERTO');
        if (ciclo) {
          this.idCicloActivo = ciclo.idCiclo;
          this.cargarDatosEstadoCuenta();
        }
      },
      error: (err) => console.error("Error al cargar el ciclo", err)
    });
  }

  cargarDatosEstadoCuenta() {
    this.pagoService.consultarEstadoCuenta(this.idEstudianteLogueado, this.idCicloActivo).subscribe({
      next: (datos) => {
        this.estadoCuenta = datos;
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert("Error al cargar el estado de cuenta: " + (err.error?.error || err.message));
      }
    });
  }
}