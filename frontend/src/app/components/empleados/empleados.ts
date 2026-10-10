import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { EmpleadoService } from '../../services/empleado.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './empleados.html'
})
export class Empleados implements OnInit {
  empleadoForm: FormGroup;
  empleadoEditForm: FormGroup; 
  listaEmpleados: any[] = [];
  crearCuentaAcceso: boolean = false;

  constructor(
    private fb: FormBuilder,
    private empleadoService: EmpleadoService,
    private cdr: ChangeDetectorRef
  ) {
    // Formulario de creacion
    this.empleadoForm = this.fb.group({
      dpi: ['', [Validators.required, Validators.maxLength(13)]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      puesto: ['', Validators.required],
      fechaContratacion: ['', Validators.required],
      salario: ['', [Validators.required, Validators.min(0)]],
      crearCuenta: [false],
      rol: [''],
      correo: [''],
      password: ['']
    });

    // Formulario de edicion
    this.empleadoEditForm = this.fb.group({
      idEmpleado: [''], 
      dpi: ['', [Validators.required, Validators.maxLength(13)]], // <-- Ahora nace habilitado
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      puesto: ['', Validators.required],
      fechaContratacion: ['', Validators.required],
      salario: ['', [Validators.required, Validators.min(0)]]
    });

    // interruptor de creacion
    this.empleadoForm.get('crearCuenta')?.valueChanges.subscribe(checked => {
      this.crearCuentaAcceso = checked;
      const rolCtrl = this.empleadoForm.get('rol');
      const correoCtrl = this.empleadoForm.get('correo');
      const passCtrl = this.empleadoForm.get('password');
      
      if (checked) {
        rolCtrl?.setValidators([Validators.required]);
        correoCtrl?.setValidators([Validators.required, Validators.email]);
        passCtrl?.setValidators([Validators.required, Validators.minLength(5)]);
      } else {
        rolCtrl?.clearValidators();
        correoCtrl?.clearValidators();
        passCtrl?.clearValidators();
        rolCtrl?.setValue('');
        correoCtrl?.setValue('');
        passCtrl?.setValue('');
      }
      rolCtrl?.updateValueAndValidity();
      correoCtrl?.updateValueAndValidity();
      passCtrl?.updateValueAndValidity();
    });
  }

  ngOnInit(): void {
    this.cargarEmpleados();
  }

  cargarEmpleados() {
    this.empleadoService.getEmpleados().subscribe({
      next: (datos) => {
        this.listaEmpleados = datos;
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error('Error al obtener empleados:', err)
    });
  }


  guardarEmpleado() {
    if (this.empleadoForm.invalid) {
      alert('Por favor, llena todos los campos correctamente.');
      return;
    }

    const formulario = { ...this.empleadoForm.value };
    delete formulario.crearCuenta;

    if (!this.crearCuentaAcceso) {
      delete formulario.rol;
      delete formulario.correo;
      delete formulario.password;
    }

    this.empleadoService.registrarPersonal(formulario).subscribe({
      next: () => {
        alert('Personal registrado exitosamente.');
        this.empleadoForm.reset({ crearCuenta: false });
        this.cargarEmpleados();
      },
      error: (err) => alert('Error al guardar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }


  abrirModalEdicion(emp: any) {
    let fechaFormateada = '';
    if (emp.fechaContratacion) {
      const anio = emp.fechaContratacion[0];
      const mes = emp.fechaContratacion[1].toString().padStart(2, '0');
      const dia = emp.fechaContratacion[2].toString().padStart(2, '0');
      fechaFormateada = `${anio}-${mes}-${dia}`;
    }

    this.empleadoEditForm.patchValue({
      idEmpleado: emp.idEmpleado,
      dpi: emp.dpi,
      nombres: emp.nombres,
      apellidos: emp.apellidos,
      puesto: emp.puesto,
      fechaContratacion: fechaFormateada,
      salario: emp.salario
    });
  }


  actualizarEmpleado() {
    if (this.empleadoEditForm.invalid) return;

    const formularioEditado = this.empleadoEditForm.getRawValue();
    const id = formularioEditado.idEmpleado;

    this.empleadoService.modificarEmpleado(id, formularioEditado).subscribe({
      next: () => {
        alert('Datos del empleado actualizados exitosamente.');
        this.cargarEmpleados();
      },
      error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    this.empleadoService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => this.cargarEmpleados(),
      error: (err) => alert('Hubo un error al cambiar el estado del empleado.')
    });
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