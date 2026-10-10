import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CarreraService } from '../../services/carrera.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-carreras',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './carreras.html'
})
export class Carreras implements OnInit {
  carreraForm: FormGroup;
  carreraEditForm: FormGroup; 
  listaCarreras: any[] = [];

  constructor(
    private fb: FormBuilder,
    private carreraService: CarreraService,
    private cdr: ChangeDetectorRef
  ) {
    // CREACIÓN
    this.carreraForm = this.fb.group({
      codigo: ['', Validators.required],
      nombre: ['', Validators.required],
      duracionAnios: ['', [Validators.required, Validators.min(1)]]
    });

    // EDICIÓN
    this.carreraEditForm = this.fb.group({
      idCarrera: [''], 
      codigo: ['', Validators.required],
      nombre: ['', Validators.required],
      duracionAnios: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.cargarCarreras();
  }

  cargarCarreras() {
    this.carreraService.getCarreras().subscribe({
      next: (datos) => {
        this.listaCarreras = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al obtener carreras:', err)
    });
  }

  guardarCarrera() {
    if (this.carreraForm.invalid) {
      alert('Llena todos los campos correctamente.');
      return;
    }

    const formulario = this.carreraForm.value;

    this.carreraService.registrarCarrera(formulario).subscribe({
      next: () => {
        alert('Carrera registrada exitosamente.');
        this.carreraForm.reset();
        this.cargarCarreras();
      },
      error: (err) => alert('Error al registrar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  abrirModalEdicion(carrera: any) {
    this.carreraEditForm.patchValue({
      idCarrera: carrera.idCarrera,
      codigo: carrera.codigo,
      nombre: carrera.nombre,
      duracionAnios: carrera.duracionAnios
    });
  }

  actualizarCarrera() {
    if (this.carreraEditForm.invalid) return;

    const formularioEditado = this.carreraEditForm.getRawValue();
    const id = formularioEditado.idCarrera;
    const inf = {
      codigo: formularioEditado.codigo,
      nombre: formularioEditado.nombre,
      duracionAnios: Number(formularioEditado.duracionAnios) 
    };

    this.carreraService.modificarCarrera(id, inf).subscribe({
      next: () => {
        alert('Carrera actualizada exitosamente.');
        this.cargarCarreras();
      },
      error: (err) => {
        console.error('Error del servidor:', err);
        alert('Error al actualizar: ' + (err.error?.error || 'Revisa la consola (F12)'));
      }
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    this.carreraService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => this.cargarCarreras(),
      error: (err) => alert('Error al cambiar el estado: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  soloNumeros(event: any): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) return false;
    return true;
  }
}