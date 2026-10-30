import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PrestamoService } from '../../services/prestamo.service';
import { CicloService } from '../../services/ciclo.service';
import { PagoService } from '../../services/pago.service';
import { EstudianteService } from '../../services/estudiante.service'; 

@Component({
  selector: 'app-tesoreria',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tesoreria.html'
})
export class Tesoreria implements OnInit {
  pestanaActiva: string = 'cobros'; 

  // Variables Multas
  multas: any[] = [];
  multasFiltradas: any[] = [];
  terminoBusqueda: string = '';
  multaSeleccionada: any = null;
  numeroFacturaCobro: string = '';
  
  idCicloActivo: number = 0;
  anioCicloActivo: number = 0;

  // Variables Anulación
  facturaAnular: string = '';
  motivoAnulacion: string = '';

  // Variables Cobros Académicos
  codigoBusquedaCobro: string = '';
  nombreBusquedaCobro: string = '';
  resultadosNombreCobro: any[] = [];
  estudianteCobro: any = null;
  conceptoCobro: string = 'INSCRIPCION';
  mesColegiatura: number | null = null;
  facturaAcademica: string = '';
  montoAcademico: number | null = null;
  descripcionAcademica: string = 'Pago de inscripción anual';

  listaMeses = [
    { id: 1, nombre: 'Enero' }, { id: 2, nombre: 'Febrero' }, { id: 3, nombre: 'Marzo' },
    { id: 4, nombre: 'Abril' }, { id: 5, nombre: 'Mayo' }, { id: 6, nombre: 'Junio' },
    { id: 7, nombre: 'Julio' }, { id: 8, nombre: 'Agosto' }, { id: 9, nombre: 'Septiembre' },
    { id: 10, nombre: 'Octubre' }, { id: 11, nombre: 'Noviembre' }, { id: 12, nombre: 'Diciembre' }
  ];

  // Variables Historial 
  historialPagos: any[] = [];
  paginaHistorial: number = 1;
  limiteHistorial: number = 10;

  constructor(
    private prestamoService: PrestamoService,
    private cicloService: CicloService,
    private pagoService: PagoService,
    private estudianteService: EstudianteService, 
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarCicloActivo();
    this.cargarMultas();
    this.cargarHistorial();
  }

  cambiarPestana(pestana: string) {
    this.pestanaActiva = pestana;
  }

  cargarCicloActivo() {
    this.cicloService.getCiclos(1, 100).subscribe({
      next: (res: any) => {
        const ciclo = res.find((c: any) => c.estado === 'ABIERTO');
        if (ciclo) {
          this.idCicloActivo = ciclo.idCiclo;
          this.anioCicloActivo = ciclo.anio; 
        }
      }
    });
  }

  // --- HISTORIAL DE PAGOS ---
  cargarHistorial() {
    this.pagoService.obtenerHistorial(this.paginaHistorial, this.limiteHistorial).subscribe({
      next: (datos: any) => {
        this.historialPagos = datos;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error('Error al cargar historial:', err)
    });
  }

  cambiarPaginaHistorial(direccion: number) {
    if (direccion === -1 && this.paginaHistorial > 1) {
      this.paginaHistorial--;
      this.cargarHistorial();
    } else if (direccion === 1 && this.historialPagos.length === this.limiteHistorial) {
      this.paginaHistorial++;
      this.cargarHistorial();
    }
  }

  imprimirFactura(numeroFactura: string) {
    this.pagoService.descargarFacturaPdf(numeroFactura).subscribe({
      next: (blob: Blob) => {
        // Crear un objeto URL con el PDF recibido
        const fileURL = URL.createObjectURL(blob);
        // Abrirlo en una nueva pestaña
        window.open(fileURL, '_blank');
      },
      error: (err: any) => {
        console.error(err);
        alert('Error al generar la factura en PDF.');
      }
    });
  }

  // --- MULTAS ---
  cargarMultas() {
    this.prestamoService.getMultasPendientes().subscribe({
      next: (datos: any) => {
        this.multas = datos;
        this.multasFiltradas = datos;
        this.cdr.detectChanges();
      }
    });
  }

  filtrarMultas() {
    const termino = this.terminoBusqueda.toLowerCase().trim();
    if (!termino) {
      this.multasFiltradas = this.multas;
    } else {
      this.multasFiltradas = this.multas.filter(m => 
        m.nombreSolicitante.toLowerCase().includes(termino) || m.tituloLibro.toLowerCase().includes(termino)
      );
    }
  }

  prepararPago(multa: any) {
    this.multaSeleccionada = multa;
    this.numeroFacturaCobro = '';
  }

  registrarPago() {
    if (!this.numeroFacturaCobro.trim() || this.idCicloActivo === 0) return;
    const payload = { numeroFactura: this.numeroFacturaCobro, idCiclo: this.idCicloActivo };

    this.prestamoService.pagarMulta(this.multaSeleccionada.idPrestamo, payload).subscribe({
      next: () => {
        alert('Pago registrado exitosamente.');
        this.terminoBusqueda = ''; 
        this.cargarMultas(); 
        this.cargarHistorial(); 
        this.cdr.detectChanges();
      },
      error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo procesar el pago.'))
    });
  }

  // --- ANULACIÓN ---
  ejecutarAnulacion() {
    if (!this.facturaAnular.trim() || !this.motivoAnulacion.trim()) return;

    if (confirm(`¿Estás seguro de anular la factura ${this.facturaAnular.toUpperCase()}? Esta acción no se puede deshacer.`)) {
      this.pagoService.anularFactura(this.facturaAnular.trim(), this.motivoAnulacion.trim()).subscribe({
        next: () => {
          alert('Factura anulada exitosamente.');
          this.facturaAnular = '';
          this.motivoAnulacion = '';
          this.cargarMultas(); 
          this.cargarHistorial(); 
        },
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo anular la factura.'))
      });
    }
  }

  // --- COBROS ACADÉMICOS ---
  buscarPorNombreCobro() {
    if (!this.nombreBusquedaCobro.trim()) return;
    this.estudianteService.buscarPorNombre(this.nombreBusquedaCobro).subscribe({
      next: (datos: any[]) => {
        this.resultadosNombreCobro = datos;
        this.cdr.detectChanges();
      },
      error: (err: any) => alert('Error: ' + (err.error?.error || err.message))
    });
  }

  seleccionarCodigoCobro(codigo: string) {
    this.codigoBusquedaCobro = codigo;
    this.resultadosNombreCobro = [];
    this.nombreBusquedaCobro = '';
    this.buscarExpedienteCobro();
  }

  buscarExpedienteCobro() {
    if (!this.codigoBusquedaCobro.trim()) return;
    this.estudianteService.obtenerExpediente(this.codigoBusquedaCobro.trim()).subscribe({
      next: (datos: any) => {
        this.estudianteCobro = datos;
        this.facturaAcademica = '';
        this.alCambiarConcepto();
        this.cdr.detectChanges();
      },
      error: () => alert('Estudiante no encontrado.')
    });
  }

  alCambiarConcepto() {
    if (!this.estudianteCobro) {
        this.montoAcademico = null;
        return;
    }

    if (this.conceptoCobro === 'INSCRIPCION') {
        this.descripcionAcademica = 'Pago de inscripción anual';
        // Asigna el monto fijo bloqueado del grado
        this.montoAcademico = this.estudianteCobro.cuotaInscripcion || 0;
    } 
    else if (this.conceptoCobro === 'COLEGIATURA') {
        this.descripcionAcademica = 'Pago de colegiatura mensual';
        // Asigna el monto fijo bloqueado del grado
        this.montoAcademico = this.estudianteCobro.cuotaMensual || 0;
    } 
    else { 
        this.descripcionAcademica = 'Pago de actividad extracurricular'; 
        this.mesColegiatura = null; 
        // Desbloquea la casilla borrando su valor, la secretaria lo teclea a mano
        this.montoAcademico = null;
    }
  }

  limpiarCobro() {
    this.estudianteCobro = null;
    this.resultadosNombreCobro = [];
    this.facturaAcademica = '';
    this.montoAcademico = null;
    this.conceptoCobro = 'INSCRIPCION';
    this.mesColegiatura = null;
    this.descripcionAcademica = 'Pago de inscripción anual';
  }

  registrarCobro() {
    if (!this.facturaAcademica.trim() || !this.montoAcademico || this.idCicloActivo === 0 || !this.estudianteCobro) return;
    if (this.conceptoCobro === 'COLEGIATURA' && (!this.mesColegiatura || this.mesColegiatura < 1)) {
        alert('Debes seleccionar el mes correspondiente a la colegiatura.');
        return;
    }

    const inf = {
      numeroFactura: this.facturaAcademica,
      idEstudiante: this.estudianteCobro.idEstudiante,
      idCiclo: this.idCicloActivo,
      concepto: this.conceptoCobro,
      monto: this.montoAcademico,
      descripcion: this.descripcionAcademica,
      mesColegiatura: this.conceptoCobro === 'COLEGIATURA' ? Number(this.mesColegiatura) : null
    };

    const peticion = this.conceptoCobro === 'INSCRIPCION' 
        ? this.pagoService.cobrarInscripcion(inf)
        : this.pagoService.cobrarPagoGeneral(inf);

    peticion.subscribe({
      next: () => {
        alert(`¡Pago de ${this.conceptoCobro.toLowerCase()} registrado exitosamente!`);
        this.limpiarCobro();
        this.codigoBusquedaCobro = '';
        this.cargarHistorial();
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        alert('Error: ' + (err.error?.error || 'No se pudo registrar el pago.'));
        this.cdr.detectChanges();
      }
    });
  }

}