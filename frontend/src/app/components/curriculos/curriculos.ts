import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { CurriculoService } from '../../services/curriculo.service';
import { CicloService } from '../../services/ciclo.service';
import { GradoService } from '../../services/grado.service';
import { CursoService } from '../../services/curso.service';

@Component({
  selector: 'app-curriculos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './curriculos.html'
})
export class Curriculos implements OnInit {
  modoVista: 'asignar' | 'reporte' = 'asignar';

  parametrosForm: FormGroup;
  
  listaCiclos: any[] = [];
  listaGrados: any[] = [];
  listaCursosActivos: any[] = []; 
  
  cursosSeleccionados: Set<number> = new Set<number>();
  mallaCargada: boolean = false;

  mallaGeneral: any[] = [];
  cicloSeleccionadoReporte: number | null = null;

  constructor(
    private fb: FormBuilder,
    private curriculoService: CurriculoService,
    private cicloService: CicloService,
    private gradoService: GradoService,
    private cursoService: CursoService,
    private cdr: ChangeDetectorRef
  ) {
    this.parametrosForm = this.fb.group({
      idCiclo: ['', Validators.required],
      idGrado: ['', Validators.required]
    });

    this.parametrosForm.valueChanges.subscribe(() => {
      this.mallaCargada = false;
    });
  }

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cambiarPestana(modo: 'asignar' | 'reporte') {
    this.modoVista = modo;
  }

  cargarCatalogos() {
    this.cicloService.getCiclos(1, 100).subscribe(datos => this.listaCiclos = datos);
    this.gradoService.getGrados(1, 100).subscribe(datos => this.listaGrados = datos);
    
    this.cursoService.getCursos(1, 100).subscribe(datos => {
      this.listaCursosActivos = datos.filter((c: any) => c.estado === true);
    });
  }

  cargarMallaCurricular() {
    if (this.parametrosForm.invalid) {
      alert('Selecciona un Ciclo Lectivo y un Grado primero.');
      return;
    }

    const idCiclo = this.parametrosForm.value.idCiclo;
    const idGrado = this.parametrosForm.value.idGrado;

    this.curriculoService.obtenerCursosCurriculo(idGrado, idCiclo).subscribe({
      next: (asignados) => {
        this.cursosSeleccionados.clear();
        asignados.forEach((curso: any) => this.cursosSeleccionados.add(curso.idCurso));
        
        this.mallaCargada = true;
        this.cdr.detectChanges();
      },
      error: (err) => alert('Error al consultar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  toggleCurso(idCurso: number, event: any) {
    if (event.target.checked) {
      this.cursosSeleccionados.add(idCurso);
    } else {
      this.cursosSeleccionados.delete(idCurso);
    }
  }

  guardarCurriculo() {
    const payload = {
      idGrado: Number(this.parametrosForm.value.idGrado),
      idCiclo: Number(this.parametrosForm.value.idCiclo),
      idsCursos: Array.from(this.cursosSeleccionados)
    };

    this.curriculoService.definirCurriculo(payload).subscribe({
      next: () => {
        alert('Malla curricular guardada exitosamente.');
        this.cargarMallaCurricular(); 
      },
      error: (err) => alert('Error al guardar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }


  cargarReporteGeneral(event: any) {
    const idCiclo = event.target.value;
    if (!idCiclo) return;

    this.cicloSeleccionadoReporte = idCiclo;
    
    this.curriculoService.obtenerMallaCompleta(idCiclo).subscribe({
      next: (datos) => {
        this.mallaGeneral = datos;
        this.cdr.detectChanges();
      },
      error: (err) => alert('Error al cargar reporte: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }
}