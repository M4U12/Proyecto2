import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { CicloService } from '../../services/ciclo.service'; 
import { GradoService } from '../../services/grado.service'; 
import { SeccionService } from '../../services/seccion.service'; 
import { EstudianteService } from '../../services/estudiante.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.html'
})
export class Reportes implements OnInit {
  reporteActivo: string = '';
  rolUsuario: string = '';

  // Catálogos
  ciclos: any[] = [];
  grados: any[] = [];
  secciones: any[] = [];

  // Variables para Formularios
  idCicloSel: number = 0;
  anioCicloSel: string = '';
  gradoSel: any = null;
  seccionSel: any = null;
  cargando: boolean = false;

  estudianteSel: any = null; 
  fechaInicioSel: string = '';
  fechaFinSel: string = '';

  codigoBusqueda: string = '';
  nombreBusqueda: string = '';
  resultadosNombre: any[] = [];
  busquedaSinResultados: boolean = false;
  
  constructor(
    private reporteService: ReporteService,
    private cicloService: CicloService,
    private gradoService: GradoService,
    private seccionService: SeccionService,
    private estudianteService: EstudianteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    // Recupera el rol del usuario logueado
    this.rolUsuario = localStorage.getItem('rol') || ''; 
    this.seleccionarReporteInicial();
    this.cargarCicloInicial();
  }

  // PERMISOS POR ROL
  puedeVerSolventes(): boolean {
    return ['SECRETARIA', 'ADMINISTRADOR', 'SUPER_ADMIN'].includes(this.rolUsuario);
  }

  puedeVerRendimiento(): boolean {
    return ['ADMINISTRADOR', 'SUPER_ADMIN'].includes(this.rolUsuario);
  }

  puedeVerIngresos(): boolean {
    return this.rolUsuario === 'SUPER_ADMIN';
  }

  puedeVerMora(): boolean {
    return ['BIBLIOTECARIO', 'ADMINISTRADOR', 'SUPER_ADMIN'].includes(this.rolUsuario);
  }

  seleccionarReporteInicial() {
    if (this.puedeVerSolventes()) {
      this.reporteActivo = 'solventes';
    } else if (this.puedeVerRendimiento()) {
      this.reporteActivo = 'rendimiento';
    } else if (this.puedeVerIngresos()) {
      this.reporteActivo = 'ingresos';
    } else if (this.puedeVerMora()) {
      this.reporteActivo = 'mora';
    }
  }

  cambiarReporte(reporte: string) {
    this.reporteActivo = reporte;
  }


  cargarCicloInicial() {
    this.cicloService.getCiclos(1, 100).subscribe(res => {
      this.ciclos = res;
      const cicloActivo = this.ciclos.find(c => c.estado === 'ABIERTO');
      if (cicloActivo) {
        this.idCicloSel = cicloActivo.idCiclo;
        this.anioCicloSel = cicloActivo.anio.toString();
        this.cargarGradosDelCiclo();
      }
      this.cdr.detectChanges();
    });
  }

  alCambiarCiclo() {
    this.gradoSel = null;
    this.seccionSel = null;
    this.secciones = [];
    this.cargarGradosDelCiclo();
  }

  cargarGradosDelCiclo() {
    if (this.idCicloSel > 0) {
      this.gradoService.getGradosPorCiclo(this.idCicloSel).subscribe(res => {
        this.grados = res;
        this.cdr.detectChanges();
      });
    }
  }

  alCambiarGrado() {
    this.seccionSel = null;
    this.secciones = [];
    
    if (this.gradoSel && this.idCicloSel > 0) {
      this.seccionService.getSeccionesPorGradoYCiclo(this.gradoSel.idGrado, this.idCicloSel).subscribe({
        next: (res) => {
          this.secciones = res;
          this.cdr.detectChanges();
        },
        error: (err) => console.error('Error al cargar secciones:', err)
      });
    }
  }

  generarReporteSolvencia() {
    if (!this.gradoSel || !this.seccionSel || !this.idCicloSel) return;
    this.cargando = true;

    this.reporteService.descargarReporteSolventes(
      this.gradoSel.idGrado,
      this.seccionSel.idSeccion,
      this.idCicloSel,
      this.gradoSel.nombre,
      this.seccionSel.identificador,
      this.anioCicloSel
    ).subscribe({
      next: (blob: Blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al generar el reporte de solvencia.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  generarReporteRendimiento() {
    if (!this.gradoSel || !this.idCicloSel) return;
    this.cargando = true;

    const idSeccionEnviar = this.seccionSel ? this.seccionSel.idSeccion : 0;
    const identificadorEnviar = this.seccionSel ? this.seccionSel.identificador : '';

    this.reporteService.descargarReporteRendimiento(
      this.gradoSel.idGrado,
      idSeccionEnviar,
      this.idCicloSel,
      this.gradoSel.nombre,
      identificadorEnviar,
      this.anioCicloSel
    ).subscribe({
      next: (blob: Blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al generar el reporte de rendimiento.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  generarReporteIngresos() {
    this.cargando = true;

    this.reporteService.descargarReporteIngresos(this.fechaInicioSel, this.fechaFinSel).subscribe({
      next: (blob: Blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al generar el reporte de ingresos.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  generarReporteMora() {
    this.cargando = true;

    this.reporteService.descargarReporteMora().subscribe({
      next: (blob: Blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al generar el reporte de mora.');
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }
}