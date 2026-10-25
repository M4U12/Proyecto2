import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CicloService } from '../../services/ciclo.service';

@Component({
  selector: 'app-ciclos',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './ciclos.html',
  styleUrl: './ciclos.css'
})
export class Ciclos implements OnInit { 
  cicloForm: FormGroup;
  cicloEditForm: FormGroup;
  listaCiclos: any[] = []; 

  constructor(private fb: FormBuilder, private cicloService: CicloService, private cdr: ChangeDetectorRef) {
    // creación
    this.cicloForm = this.fb.group({
      anio: ['', [Validators.required, Validators.min(2026), Validators.pattern('^[0-9]+$')]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });

    // edición
    this.cicloEditForm = this.fb.group({
      idCiclo: [''], 
      anio: ['', [Validators.required, Validators.min(2026), Validators.pattern('^[0-9]+$')]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.cargarCiclos();
  }

  paginaActual: number = 1;
  limitePorPagina: number = 5;
  cargarCiclos() {
    this.cicloService.getCiclos(this.paginaActual, this.limitePorPagina).subscribe({
      next: (datos) => {
        this.listaCiclos = datos;
        this.cdr.detectChanges(); 
      },
      error: (err: any) => console.error('Error al obtener ciclos:', err)
    });
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarCiclos();
    }
  }

  soloNumeros(event: KeyboardEvent) {
    const charCode = event.key.charCodeAt(0);
    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
    }
  }

  // Guarda nuevo ciclo
  onSubmit() {
    if (this.cicloForm.valid) {
      const formulario = this.cicloForm.value;
      const anioLectivo = Number(formulario.anio);
      const anioInicio = Number(formulario.fechaInicio.substring(0, 4));
      const anioFin = Number(formulario.fechaFin.substring(0, 4));

      if (formulario.fechaInicio >= formulario.fechaFin) {
        alert('Error: La fecha de inicio debe ser anterior a la fecha de finalización.');
        return;
      }

      // validar coincidencia de año
      if (anioInicio !== anioLectivo || anioFin !== anioLectivo) {
        alert(`Error: Las fechas seleccionadas deben pertenecer al año lectivo ${anioLectivo}.`);
        return;
      }

      this.cicloService.crearCiclo(formulario).subscribe({
        next: (respuesta) => {
          alert('Ciclo creado exitosamente');
          this.cicloForm.reset();
          this.paginaActual = 1;
          this.cargarCiclos(); 
        },
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo crear el ciclo'))
      });
    }
  }

  // Cargar datos al modal
  abrirModalEdicion(ciclo: any) {
    let inicioFormateada = '';
    let finFormateada = '';

    if (ciclo.fechaInicio && Array.isArray(ciclo.fechaInicio)) {
      inicioFormateada = `${ciclo.fechaInicio[0]}-${ciclo.fechaInicio[1].toString().padStart(2, '0')}-${ciclo.fechaInicio[2].toString().padStart(2, '0')}`;
    } else {
        inicioFormateada = ciclo.fechaInicio;
    }

    if (ciclo.fechaFin && Array.isArray(ciclo.fechaFin)) {
      finFormateada = `${ciclo.fechaFin[0]}-${ciclo.fechaFin[1].toString().padStart(2, '0')}-${ciclo.fechaFin[2].toString().padStart(2, '0')}`;
    } else {
        finFormateada = ciclo.fechaFin;
    }

    this.cicloEditForm.patchValue({
      idCiclo: ciclo.idCiclo,
      anio: ciclo.anio,
      fechaInicio: inicioFormateada,
      fechaFin: finFormateada
    });
  }

  // Actualizar ciclo
  actualizarCiclo() {
    if (this.cicloEditForm.invalid) return;

    const formularioEditado = this.cicloEditForm.getRawValue();
    const id = formularioEditado.idCiclo;
    const anioLectivo = Number(formularioEditado.anio);
    const anioInicio = Number(formularioEditado.fechaInicio.substring(0, 4));
    const anioFin = Number(formularioEditado.fechaFin.substring(0, 4));

    if (formularioEditado.fechaInicio >= formularioEditado.fechaFin) {
      alert('Error: La fecha de inicio debe ser anterior a la fecha de finalización.');
      return;
    }

    if (anioInicio !== anioLectivo || anioFin !== anioLectivo) {
      alert(`Error: Las fechas seleccionadas deben pertenecer al año lectivo ${anioLectivo}.`);
      return;
    }

    const inf = {
      anio: anioLectivo,
      fechaInicio: formularioEditado.fechaInicio,
      fechaFin: formularioEditado.fechaFin
    };

    this.cicloService.modificarCiclo(id, inf).subscribe({
      next: () => {
        alert('Ciclo actualizado exitosamente.');
        this.cargarCiclos();
      },
      error: (err: any) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  // Cambiar estado
  abrirCiclo(id: number) {
    if(confirm('¿Estás seguro de abrir este ciclo?')) {
      this.cicloService.abrirCiclo(id).subscribe({
        next: () => this.cargarCiclos(),
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo abrir')) 
      });
    }
  }

  cerrarCiclo(id: number) {
    if(confirm('¿Estás seguro de cerrar este ciclo?')) {
      this.cicloService.cerrarCiclo(id).subscribe({
        next: () => this.cargarCiclos(),
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo cerrar')) 
      });
    }
  }

  // Genera la fecha mínima (1 de enero del año ingresado)
  getMinDate(formulario: FormGroup): string {
    const anio = formulario.get('anio')?.value;
    return (anio && anio >= 2026) ? `${anio}-01-01` : '';
  }

  // Genera la fecha máxima (31 de diciembre del año ingresado)
  getMaxDate(formulario: FormGroup): string {
    const anio = formulario.get('anio')?.value;
    return (anio && anio >= 2026) ? `${anio}-10-31` : ''; 
  }
}