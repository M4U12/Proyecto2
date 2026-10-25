import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { EstudianteService } from '../../services/estudiante.service';
import { CicloService } from '../../services/ciclo.service';
import { PagoService } from '../../services/pago.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-estudiantes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './estudiantes.html'
})
export class Estudiantes implements OnInit {
  estudianteForm: FormGroup;
  
  codigoBusqueda: string = '';
  nombreBusqueda: string = '';
  
  expedienteActual: any = null;
  modoEdicion: boolean = false;
  resultadosNombre: any[] = [];
  busquedaSinResultados: boolean = false;
  
  idCicloActivo: number = 0;

  constructor(
    private fb: FormBuilder,
    private estudianteService: EstudianteService,
    private cicloService: CicloService,
    private pagoService: PagoService,
    private cdr: ChangeDetectorRef
  ) {
    this.estudianteForm = this.fb.group({
      codigoPersonal: ['', [Validators.required, Validators.minLength(4)]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      fechaNacimiento: ['', Validators.required],
      informacionMedica: ['Ninguna'],
      nombreEncargado: ['', Validators.required],
      contactoEncargado: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  ngOnInit(): void {
    this.cargarCicloActivo();
  }

  cargarCicloActivo() {
    this.cicloService.getCiclos(1, 100).subscribe({
      next: (res: any) => {
        const ciclo = res.find((c: any) => c.estado === 'ABIERTO');
        if (ciclo) {
          this.idCicloActivo = ciclo.idCiclo;
        }
      }
    });
  }

  generarSolvencia() {
    if (!this.expedienteActual || this.idCicloActivo === 0) {
      alert("No se puede generar la solvencia. Asegúrate de tener un alumno seleccionado y un ciclo activo.");
      return;
    }

    this.pagoService.descargarSolvenciaPdf(this.expedienteActual.idEstudiante, this.idCicloActivo).subscribe({
      next: (blob: Blob) => {
        const fileURL = URL.createObjectURL(blob);
        window.open(fileURL, '_blank');
      },
      error: (err: any) => {
        // Extraeel texto del Blob que devuelve Angular
        if (err.error instanceof Blob) {
          err.error.text().then((mensajeDeuda: string) => {
             alert("NO SE PUEDE EMITIR SOLVENCIA:\n\n" + mensajeDeuda);
          });
        } else {
           alert("Error al verificar la solvencia del estudiante.");
        }
      }
    });
  }

  buscarPorNombre() {
    if (!this.nombreBusqueda.trim()) {
      alert('Ingresa un nombre para buscar.');
      return;
    }

    this.resultadosNombre = [];
    this.busquedaSinResultados = false;
    this.expedienteActual = null;
    this.modoEdicion = false;
    this.cdr.detectChanges(); 

    this.estudianteService.buscarPorNombre(this.nombreBusqueda).subscribe({
      next: (datos: any[]) => {
        this.resultadosNombre = datos;
        if (this.resultadosNombre.length === 0) {
          this.busquedaSinResultados = true;
        }
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        alert('Error al buscar: ' + (err.error?.error || err.message));
        this.cdr.detectChanges();
      }
    });
  }

  seleccionarCodigo(codigo: string) {
    this.codigoBusqueda = codigo;
    this.resultadosNombre = []; 
    this.nombreBusqueda = '';   
    this.cdr.detectChanges();
    this.buscarExpediente();    
  }

  buscarExpediente() {
    if (!this.codigoBusqueda.trim()) {
      alert('Ingresa un código personal para buscar.');
      return;
    }

    this.expedienteActual = null;
    this.modoEdicion = false;
    this.resultadosNombre = []; 
    this.busquedaSinResultados = false;
    this.cdr.detectChanges(); 

    this.estudianteService.obtenerExpediente(this.codigoBusqueda.trim()).subscribe({
      next: (datos) => {
        this.expedienteActual = datos;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        alert('Estudiante no encontrado: ' + (err.error?.error || 'Verifica el código.'));
        this.cdr.detectChanges();
      }
    });
  }

  guardarEstudiante() {
    if (this.estudianteForm.invalid) {
      alert('Por favor, llena todos los campos obligatorios.');
      return;
    }

    const formulario = this.estudianteForm.value;

    if (this.modoEdicion && this.expedienteActual) {
      this.estudianteService.modificarEstudiante(this.expedienteActual.codigoPersonal, formulario).subscribe({
        next: () => {
          alert('Expediente actualizado exitosamente.');
          this.modoEdicion = false;
          this.codigoBusqueda = formulario.codigoPersonal;
          this.buscarExpediente();
        },
        error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
      });
    } else {
      this.estudianteService.registrarEstudiante(formulario).subscribe({
        next: () => {
          alert('Estudiante registrado exitosamente.');
          this.codigoBusqueda = formulario.codigoPersonal;
          this.estudianteForm.reset({ informacionMedica: 'Ninguna' });
          this.buscarExpediente();
        },
        error: (err) => alert('Error al registrar: ' + (err.error?.error || 'Intenta de nuevo.'))
      });
    }
  }

  prepararEdicion() {
    this.modoEdicion = true;
    let fechaFormateada = '';
    
    if (this.expedienteActual.fechaNacimiento) {
      if (Array.isArray(this.expedienteActual.fechaNacimiento)) {
        const anio = this.expedienteActual.fechaNacimiento[0];
        const mes = this.expedienteActual.fechaNacimiento[1].toString().padStart(2, '0');
        const dia = this.expedienteActual.fechaNacimiento[2].toString().padStart(2, '0');
        fechaFormateada = `${anio}-${mes}-${dia}`;
      } else {
        fechaFormateada = this.expedienteActual.fechaNacimiento;
      }
    }

    this.estudianteForm.patchValue({
      codigoPersonal: this.expedienteActual.codigoPersonal,
      nombres: this.expedienteActual.nombres,
      apellidos: this.expedienteActual.apellidos,
      fechaNacimiento: fechaFormateada,
      informacionMedica: this.expedienteActual.informacionMedica,
      nombreEncargado: this.expedienteActual.nombreEncargado, 
      contactoEncargado: this.expedienteActual.contactoEncargado
    });
    
    this.cdr.detectChanges(); 
  }

  cancelarEdicion() {
    this.modoEdicion = false;
    this.estudianteForm.reset({ informacionMedica: 'Ninguna' });
    this.cdr.detectChanges();
  }

  soloNumeros(event: any): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) return false;
    return true;
  }

  soloLetras(event: KeyboardEvent): void {
    const tecla = event.key;
    const letrasValidas = /^[a-zA-Z\sñÑáéíóúÁÉÍÓÚ]+$/;
    if (!letrasValidas.test(tecla)) {
      event.preventDefault(); 
    }
  }
}