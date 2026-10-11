import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CursoService } from '../../services/curso.service';

@Component({
  selector: 'app-cursos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cursos.html'
})
export class Cursos implements OnInit {
  cursoForm: FormGroup;
  cursoEditForm: FormGroup;
  listaCursos: any[] = [];

  constructor(
    private fb: FormBuilder,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef
  ) {
    // FORMULARIO DE CREACIÓN
    this.cursoForm = this.fb.group({
      codigo: ['', Validators.required], 
      nombre: ['', Validators.required]  
    });

    // FORMULARIO DE EDICIÓN
    this.cursoEditForm = this.fb.group({
      idCurso: [''],
      codigo: ['', Validators.required],
      nombre: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarCursos();
  }

  paginaActual: number = 1;
  limitePorPagina: number = 10;
  cargarCursos() {
    this.cursoService.getCursos(this.paginaActual, this.limitePorPagina).subscribe({
      next: (datos) => {
        this.listaCursos = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al obtener cursos:', err)
    });
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarCursos();
    }
  }

  guardarCurso() {
    if (this.cursoForm.invalid) return;

    this.cursoService.registrarCurso(this.cursoForm.value).subscribe({
      next: () => {
        alert('Curso registrado exitosamente.');
        this.cursoForm.reset();
        this.paginaActual = 1; 
        this.cargarCursos();
      },
      error: (err) => alert('Error al guardar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  abrirModalEdicion(curso: any) {
    this.cursoEditForm.patchValue({
      idCurso: curso.idCurso,
      codigo: curso.codigo,
      nombre: curso.nombre
    });
  }

  actualizarCurso() {
    if (this.cursoEditForm.invalid) return;

    const formulario = this.cursoEditForm.getRawValue();
    const payload = {
      codigo: formulario.codigo,
      nombre: formulario.nombre
    };

    this.cursoService.modificarCurso(formulario.idCurso, payload).subscribe({
      next: () => {
        alert('Curso actualizado exitosamente.');
        this.cargarCursos();
      },
      error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    this.cursoService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => this.cargarCursos(),
      error: (err) => alert('Error al cambiar estado: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }
}