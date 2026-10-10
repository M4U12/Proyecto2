import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NivelService } from '../../services/nivel.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-niveles',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './niveles.html'
})
export class Niveles implements OnInit {
  listaNiveles: any[] = [];
  nivelEditForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private nivelService: NivelService,
    private cdr: ChangeDetectorRef
  ) {
    this.nivelEditForm = this.fb.group({
      idNivel: [''],
      nivel: [{ value: '', disabled: true }], 
      descripcion: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarNiveles();
  }

  cargarNiveles() {
    this.nivelService.getNiveles().subscribe({
      next: (datos) => {
        this.listaNiveles = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al obtener niveles:', err)
    });
  }

  abrirModalEdicion(nivel: any) {
    this.nivelEditForm.patchValue({
      idNivel: nivel.idNivel,
      nivel: nivel.nivel,
      descripcion: nivel.descripcion
    });
  }

  actualizarNivel() {
    if (this.nivelEditForm.invalid) return;

    const formulario = this.nivelEditForm.getRawValue();
    
    const inf = {
      descripcion: formulario.descripcion
    };

    this.nivelService.modificarNivel(formulario.idNivel, inf).subscribe({
      next: () => {
        alert('Descripción actualizada exitosamente.');
        this.cargarNiveles();
      },
      error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    this.nivelService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => this.cargarNiveles(),
      error: (err) => alert('Error al cambiar el estado.')
    });
  }
}