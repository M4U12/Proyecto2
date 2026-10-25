import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { GradoService } from '../../services/grado.service';
import { NivelService } from '../../services/nivel.service';
import { CarreraService } from '../../services/carrera.service';
import { SeccionService } from '../../services/seccion.service';
import { CicloService } from '../../services/ciclo.service';

@Component({
  selector: 'app-grados',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './grados.html'
})
export class Grados implements OnInit {
  gradoForm: FormGroup;
  gradoEditForm: FormGroup;
  nuevaSeccionForm: FormGroup;
  
  listaGrados: any[] = [];
  listaNiveles: any[] = [];
  listaCarreras: any[] = [];
  listaCiclos: any[] = []; 
  
  gradoSeleccionado: any = null; 
  listaSeccionesGrado: any[] = [];
  
  paginaActual: number = 1;
  limitePorPagina: number = 5;

  constructor(
    private fb: FormBuilder,
    private gradoService: GradoService,
    private nivelService: NivelService,
    private carreraService: CarreraService,
    private seccionService: SeccionService,
    private cicloService: CicloService, 
    private cdr: ChangeDetectorRef
  ) {
    this.gradoForm = this.fb.group({
      nombre: ['', Validators.required],
      idNivel: ['', Validators.required],
      idCarrera: [{ value: '', disabled: true }], 
      idCiclo: ['', Validators.required], 
      secciones: this.fb.array([]) 
    });

    this.gradoEditForm = this.fb.group({
      idGrado: [''],
      nombre: ['', Validators.required],
      idNivel: ['', Validators.required],
      idCarrera: [{ value: '', disabled: true }] 
    });

    this.nuevaSeccionForm = this.fb.group({
      idCiclo: ['', Validators.required], 
      identificador: ['', Validators.required],
      cupo: [30, [Validators.required, Validators.min(1)]]
    });

    this.gradoForm.get('idNivel')?.valueChanges.subscribe(id => this.verificarCarrera(id, this.gradoForm));
    this.gradoEditForm.get('idNivel')?.valueChanges.subscribe(id => this.verificarCarrera(id, this.gradoEditForm));
    
    // Al cambiar de ciclo en el modal, buscar sus secciones automáticamente
    this.nuevaSeccionForm.get('idCiclo')?.valueChanges.subscribe(idCiclo => {
      if (idCiclo) this.cargarSeccionesDelGradoSeleccionado();
    });
  }

  ngOnInit(): void {
    this.cargarCatalogos();
    this.agregarSeccion(); 
  }

  cargarCatalogos() {
    this.nivelService.getNiveles().subscribe(datos => {
      this.listaNiveles = datos.filter((n: any) => n.estado === true);
    });
    this.carreraService.getCarreras().subscribe(datos => {
      this.listaCarreras = datos.filter((c: any) => c.estado === true);
    });
    this.cicloService.getCiclos(1, 100).subscribe(res => {
      this.listaCiclos = res.filter((c: any) => c.estado === 'ABIERTO' || c.estado === true);
    });
    this.cargarGrados();
  }

  cargarGrados() {
    this.gradoService.getGrados(this.paginaActual, this.limitePorPagina).subscribe({
      next: (datos) => {
        this.listaGrados = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al obtener grados:', err)
    });
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarGrados();
    }
  }

  getNombreNivel(idNivel: number): string {
    const nivel = this.listaNiveles.find(n => n.idNivel === idNivel);
    return nivel ? nivel.nivel : 'Desconocido';
  }

  getNombreCarrera(idCarrera: number): string {
    if (!idCarrera) return 'No aplica';
    const carrera = this.listaCarreras.find(c => c.idCarrera === idCarrera);
    return carrera ? carrera.nombre : 'Desconocida';
  }

  verificarCarrera(idNivel: any, formulario: FormGroup) {
    const nivelSeleccionado = this.listaNiveles.find(n => n.idNivel == idNivel);
    const controlCarrera = formulario.get('idCarrera');
    
    if (nivelSeleccionado && nivelSeleccionado.nivel === 'DIVERSIFICADO') {
      controlCarrera?.enable();
    } else {
      controlCarrera?.disable();
      controlCarrera?.setValue(''); 
    }
  }

  get secciones() {
    return this.gradoForm.get('secciones') as FormArray;
  }

  agregarSeccion() {
    const seccionGroup = this.fb.group({
      identificador: ['', Validators.required],
      cupo: [30, [Validators.required, Validators.min(1)]]
    });
    this.secciones.push(seccionGroup);
  }

  removerSeccion(index: number) {
    if (this.secciones.length > 1) {
      this.secciones.removeAt(index);
    } else {
      alert('Un grado debe tener al menos una sección obligatoria.');
    }
  }

  guardarGrado() {
    if (this.gradoForm.invalid) {
      alert('Por favor completa todos los campos requeridos.');
      return;
    }

    const formularioBruto = this.gradoForm.getRawValue();
    
    const inf = {
      nombre: formularioBruto.nombre,
      idNivel: Number(formularioBruto.idNivel),
      idCarrera: formularioBruto.idCarrera ? Number(formularioBruto.idCarrera) : null,
      idCiclo: Number(formularioBruto.idCiclo),
      secciones: formularioBruto.secciones
    };

    this.gradoService.registrarGrado(inf).subscribe({
      next: () => {
        alert('Grado y secciones registrados exitosamente.');
        this.gradoForm.reset();
        this.secciones.clear();
        this.agregarSeccion(); 
        this.paginaActual = 1;
        this.cargarGrados();
      },
      error: (err) => alert('Error al guardar: ' + (err.error?.error || 'Revisa la consola'))
    });
  }

  // EDICIÓN DE GRADO MODAL 1
  abrirModalEdicion(grado: any) {
    this.gradoEditForm.patchValue({
      idGrado: grado.idGrado,
      nombre: grado.nombre,
      idNivel: grado.idNivel,
      idCarrera: grado.idCarrera || ''
    });
  }

  actualizarGrado() {
    if (this.gradoEditForm.invalid) return;

    const formularioEditado = this.gradoEditForm.getRawValue();
    const id = formularioEditado.idGrado;

    const payload = {
      nombre: formularioEditado.nombre,
      idNivel: Number(formularioEditado.idNivel),
      idCarrera: formularioEditado.idCarrera ? Number(formularioEditado.idCarrera) : null
    };

    this.gradoService.modificarGrado(id, payload).subscribe({
      next: () => {
        alert('Grado actualizado exitosamente.');
        this.cargarGrados();
      },
      error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  // GESTIÓN DE SECCIONES INDIVIDUALES MODAL 2
  abrirModalSecciones(grado: any) {
    this.gradoSeleccionado = grado;
    this.listaSeccionesGrado = []; 
    
    const cicloActivo = this.listaCiclos.find(c => c.estado === 'ABIERTO');
    const idPreseleccionado = cicloActivo ? cicloActivo.idCiclo : '';

    this.nuevaSeccionForm.reset({ cupo: 30, idCiclo: idPreseleccionado });

    if (idPreseleccionado) {
      this.cargarSeccionesDelGradoSeleccionado();
    }
  }

  cargarSeccionesDelGradoSeleccionado() {
    const idCiclo = this.nuevaSeccionForm.value.idCiclo;
    if (!this.gradoSeleccionado || !idCiclo) return;
    
    this.seccionService.getSeccionesPorGradoYCiclo(this.gradoSeleccionado.idGrado, Number(idCiclo)).subscribe({
      next: (datos) => {
        this.listaSeccionesGrado = datos;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar secciones', err)
    });
  }

  guardarNuevaSeccion() {
    if (this.nuevaSeccionForm.invalid || !this.gradoSeleccionado) return;

    const payload = {
      idGrado: this.gradoSeleccionado.idGrado,
      idCiclo: Number(this.nuevaSeccionForm.value.idCiclo), 
      identificador: this.nuevaSeccionForm.value.identificador,
      cupo: this.nuevaSeccionForm.value.cupo
    };

    this.seccionService.agregarSeccion(payload).subscribe({
      next: () => {
        this.cargarSeccionesDelGradoSeleccionado(); // Recarga la tabla de ese ciclo
        this.nuevaSeccionForm.patchValue({ identificador: '', cupo: 30 }); // Solo borra los campos de texto, mantiene el ciclo seleccionado
      },
      error: (err) => alert('Error: ' + (err.error?.error || 'Intenta de nuevo'))
    });
  }

  eliminarSeccion(idSeccion: number) {
    if (confirm('¿Estás seguro de eliminar esta sección?')) {
      this.seccionService.eliminarSeccion(idSeccion).subscribe({
        next: () => {
          this.cargarSeccionesDelGradoSeleccionado();
        },
        error: (err) => alert('Error: ' + (err.error?.error || 'No se pudo eliminar'))
      });
    }
  }
}