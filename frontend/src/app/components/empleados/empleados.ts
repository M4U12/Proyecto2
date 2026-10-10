import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { EmpleadoService } from '../../services/empleado.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-empleados',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './empleados.html',
  styleUrl: './empleados.css'
})
export class Empleados implements OnInit {
  empleadoForm: FormGroup;
  listaEmpleados: any[] = [];
  crearCuentaAcceso: boolean = false;

  constructor(
    private fb: FormBuilder,
    private empleadoService: EmpleadoService,
    private cdr: ChangeDetectorRef
  ) {
    this.empleadoForm = this.fb.group({
      // Datos de Empleado
      dpi: ['', [Validators.required, Validators.maxLength(13)]],
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      puesto: ['', Validators.required], 
      fechaContratacion: ['', Validators.required],
      salario: ['', [Validators.required, Validators.min(0)]],
      
      // Control de cuenta
      crearCuenta: [false],

      rol: [''], 
      correo: [''],
      password: ['']
    });

    // interruptor apagado o encendido
    this.empleadoForm.get('crearCuenta')?.valueChanges.subscribe(checked => {
      this.crearCuentaAcceso = checked;
      const rolCtrl = this.empleadoForm.get('rol');
      const correoCtrl = this.empleadoForm.get('correo');
      const passCtrl = this.empleadoForm.get('password');
      
      if (checked) {
        // Hace obligatorios los campos de la cuenta
        rolCtrl?.setValidators([Validators.required]);
        correoCtrl?.setValidators([Validators.required, Validators.email]);
        passCtrl?.setValidators([Validators.required, Validators.minLength(5)]);
      } else {
        // Limpia validadores 
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

    // clon para que los datos no se borren frente al usuario
    const formulario = { ...this.empleadoForm.value };
    
    // Para borrar el interruptor para que java no lo vea y no colapse
    delete formulario.crearCuenta;
    
    // si no quiere crear cuenta, borram los campos de seguridad
    if (!this.crearCuentaAcceso) {
      delete formulario.rol;
      delete formulario.correo;
      delete formulario.password;
    }

    this.empleadoService.registrarPersonal(formulario).subscribe({
      next: (respuesta) => {
        alert('Personal registrado exitosamente.');
        this.empleadoForm.reset({ crearCuenta: false });
        this.cargarEmpleados();
      },
      error: (err) => {
        console.error('Error completo del servidor:', err); // Para que lo veas en consola
        alert('Error al guardar: ' + (err.error?.error || 'Intenta de nuevo. Revisa F12.'));
      }
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    const nuevoEstado = !estadoActual;
    this.empleadoService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => {
        this.cargarEmpleados();
      },
      error: (err) => {
        console.error('Error al cambiar estado:', err);
        alert('Hubo un error al cambiar el estado del empleado.');
      }
    });
  }

  soloNumeros(event: any): boolean {
    const charCode = (event.which) ? event.which : event.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
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