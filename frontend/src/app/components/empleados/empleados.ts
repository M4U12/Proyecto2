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

  miRolActual: string = '';
  soySuperAdmin: boolean = false;

  paginaActual: number = 1;
  limitePorPagina: number = 10;
  miIdEmpleado: number = 0;
  constructor(
    private fb: FormBuilder,
    private empleadoService: EmpleadoService,
    private cdr: ChangeDetectorRef
  ) {
    // identifica quién está usando la pantalla
    this.miRolActual = localStorage.getItem('rol') || '';
    this.soySuperAdmin = this.miRolActual === 'SUPER_ADMIN';
    this.miIdEmpleado = Number(localStorage.getItem('idEmpleado')) || 0;

    // Formulario de creación
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

    // Formulario de edición
    this.empleadoEditForm = this.fb.group({
      idEmpleado: [''], 
      dpi: ['', [Validators.required, Validators.maxLength(13)]], 
      nombres: ['', Validators.required],
      apellidos: ['', Validators.required],
      puesto: ['', Validators.required],
      fechaContratacion: ['', Validators.required],
      salario: ['', [Validators.required, Validators.min(0)]]
    });

    // Configuración dinámica de validaciones según el rol
    if (this.soySuperAdmin) {
      // El Super Admin siempre crea cuentas obligatoriamente
      this.crearCuentaAcceso = true;
      this.empleadoForm.get('crearCuenta')?.setValue(true);
      this.empleadoForm.get('rol')?.setValidators([Validators.required]);
      this.empleadoForm.get('correo')?.setValidators([Validators.required, Validators.email]);
      this.empleadoForm.get('password')?.setValidators([Validators.required, Validators.minLength(5)]);
    } else {
      // El Admin normal usa el switch
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
  }

  ngOnInit(): void {
    this.cargarEmpleados();
  }

  cargarEmpleados() {
    this.empleadoService.getEmpleados(this.paginaActual, this.limitePorPagina).subscribe({
      next: (datos) => {
        // Filtra usando la propiedad rolSistema
        if (this.soySuperAdmin) {
          this.listaEmpleados = datos.filter((e: any) => e.rolSistema === 'SUPER_ADMIN' || e.rolSistema === 'ADMINISTRADOR');
        } else {
          // El admin ve al resto del personal tengan cuenta o no
          this.listaEmpleados = datos.filter((e: any) => e.rolSistema !== 'SUPER_ADMIN' && e.rolSistema !== 'ADMINISTRADOR');
        }
        this.cdr.detectChanges(); 
      },
      error: (err) => console.error('Error al obtener empleados:', err)
    });
  }

  cambiarPagina(valor: number) {
    if (this.paginaActual + valor > 0) {
      this.paginaActual += valor;
      this.cargarEmpleados();
    }
  }

  guardarEmpleado() {
    if (this.empleadoForm.invalid) {
      alert('Por favor, llena todos los campos correctamente.');
      return;
    }

    const formulario = { ...this.empleadoForm.value };
    delete formulario.crearCuenta;

    if (!this.soySuperAdmin && !this.crearCuentaAcceso) {
      delete formulario.rol;
      delete formulario.correo;
      delete formulario.password;
    }

    this.empleadoService.registrarPersonal(formulario).subscribe({
      next: () => {
        alert('Registro guardado exitosamente.');
        
        // Reseteo 
        this.empleadoForm.reset({ crearCuenta: this.soySuperAdmin });
        if (!this.soySuperAdmin) this.crearCuentaAcceso = false;
        
        this.paginaActual = 1;
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
        alert('Datos actualizados exitosamente.');
        this.cargarEmpleados();
      },
      error: (err) => alert('Error al actualizar: ' + (err.error?.error || 'Intenta de nuevo.'))
    });
  }

  cambiarEstado(id: number, estadoActual: boolean) {
    if (id === this.miIdEmpleado && estadoActual === true) {
      alert('Por seguridad, no puedes dar de baja tu propia cuenta.');
      return;
    }

    const nuevoEstado = !estadoActual;
    this.empleadoService.cambiarEstado(id, nuevoEstado).subscribe({
      next: () => this.cargarEmpleados(),
      error: (err: any) => alert('Error: ' + (err.error?.error || 'No se pudo cambiar el estado.'))
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