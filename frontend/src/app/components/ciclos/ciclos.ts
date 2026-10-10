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
  listaCiclos: any[] = []; 

  constructor(private fb: FormBuilder, private cicloService: CicloService, private cdr: ChangeDetectorRef) {
    this.cicloForm = this.fb.group({
      anio: ['', [Validators.required, Validators.min(2026), Validators.pattern('^[0-9]+$')]],
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
    });
  }

  // Se ejecuta automáticamente al abrir la pantalla
  ngOnInit() {
    this.cargarCiclos();
  }

  // Pide la lista a java
  cargarCiclos() {
    this.cicloService.getCiclos().subscribe({
      next: (datos) => {
        this.listaCiclos = datos;
        this.cdr.detectChanges(); 
      },
      error: (err: any) => console.error('Error al obtener ciclos:', err)
    });
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
      this.cicloService.crearCiclo(this.cicloForm.value).subscribe({
        next: (respuesta) => {
          alert('Ciclo creado exitosamente');
          this.cicloForm.reset();
          this.cargarCiclos(); 
        },
        error: (err: any) => { // <-- Le agregamos ": any"
          alert('Error: ' + (err.error?.error || 'No se pudo crear el ciclo'));
        }
      });
    }
  }

  // Cambiar estado
  abrirCiclo(id: number) {
    if(confirm('¿Estás seguro de abrir este ciclo?')) {
      this.cicloService.abrirCiclo(id).subscribe({
        next: () => this.cargarCiclos(),
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo abrir')) // <-- Le agregamos ": any"
      });
    }
  }

  cerrarCiclo(id: number) {
    if(confirm('¿Estás seguro de cerrar este ciclo?')) {
      this.cicloService.cerrarCiclo(id).subscribe({
        next: () => this.cargarCiclos(),
        error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo cerrar')) // <-- Le agregamos ": any"
      });
    }
  }
}