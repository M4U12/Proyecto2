import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LibroService } from '../../services/libro.service';

@Component({
  selector: 'app-gestion-libros',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './gestion-libros.html'
})
export class GestionLibros implements OnInit {
  listaLibros: any[] = [];
  paginaActual: number = 1;
  limitePorPagina: number = 10;
  filtroBusqueda: string = '';
  cargando: boolean = false;
  libroForm: FormGroup;
  mostrarFormulario: boolean = false;
  modoEdicion: boolean = false;
  idLibroSeleccionado: number = 0;
  imagenBase64: string | null = null;

  constructor(
    private fb: FormBuilder,
    private libroService: LibroService,
    private cdr: ChangeDetectorRef
  ) {
    this.libroForm = this.fb.group({
      codigo: ['', Validators.required],
      titulo: ['', Validators.required],
      autor: ['', Validators.required],
      editorial: [''],
      copiasTotales: [1, [Validators.required, Validators.min(1)]],
      imagen: ['']
    });
  }

  ngOnInit(): void {
    this.cargarLibros();
  }

  cargarLibros() {
    this.cargando = true;
    this.libroService.listarLibros(this.paginaActual, this.limitePorPagina, this.filtroBusqueda).subscribe({
      next: (datos) => {
        this.listaLibros = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar libros:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  buscar() {
    this.paginaActual = 1;
    this.cargarLibros();
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarLibros();
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.imagenBase64 = e.target.result;
        this.libroForm.patchValue({ imagen: this.imagenBase64 });
        this.cdr.detectChanges();
      };
      reader.readAsDataURL(file);
    }
  }

  removerImagen() {
    this.imagenBase64 = null;
    this.libroForm.patchValue({ imagen: null });
    // Limpia el input file visualmente si es necesario
    const input = document.getElementById('inputFile') as HTMLInputElement;
    if (input) input.value = '';
  }

  abrirNuevo() {
    this.mostrarFormulario = true;
    this.modoEdicion = false;
    this.idLibroSeleccionado = 0;
    this.imagenBase64 = null;
    this.libroForm.reset({ copiasTotales: 1 });
  }

  abrirEdicion(libro: any) {
    this.mostrarFormulario = true;
    this.modoEdicion = true;
    this.idLibroSeleccionado = libro.idLibro;
    this.imagenBase64 = libro.imagen;
    
    this.libroForm.patchValue({
      codigo: libro.codigo,
      titulo: libro.titulo,
      autor: libro.autor,
      editorial: libro.editorial,
      copiasTotales: libro.copiasTotales,
      imagen: libro.imagen
    });
  }

  cerrarFormulario() {
    this.mostrarFormulario = false;
  }

  guardarLibro() {
    if (this.libroForm.invalid) return;

    const datos = this.libroForm.value;

    if (this.modoEdicion) {
      this.libroService.modificarLibro(this.idLibroSeleccionado, datos).subscribe({
        next: () => {
          alert('Libro actualizado correctamente.');
          this.cerrarFormulario();
          this.cargarLibros();
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo actualizar el libro.'))
      });
    } else {
      this.libroService.registrarLibro(datos).subscribe({
        next: () => {
          alert('Libro registrado correctamente.');
          this.cerrarFormulario();
          this.cargarLibros();
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'Revisa si el código ya existe.'))
      });
    }
  }

  cambiarEstado(idLibro: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    const accion = nuevoEstado ? 'ACTIVAR' : 'DESACTIVAR';
    
    if (confirm(`¿Estás seguro de ${accion} este libro?`)) {
      this.libroService.cambiarEstado(idLibro, nuevoEstado).subscribe({
        next: () => {
          this.cargarLibros();
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo cambiar el estado.'))
      });
    }
  }
}