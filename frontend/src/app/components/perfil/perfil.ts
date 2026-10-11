import { Component, OnInit, ChangeDetectorRef} from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UsuarioService } from '../../services/usuario.service';
import { EmpleadoService } from '../../services/empleado.service'; 
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './perfil.html'
})
export class Perfil implements OnInit {
  passwordForm: FormGroup;
  
  idUsuarioActual: number = 0;
  rolUsuario: string = '';
  correoActual: string = ''; 
  nombreActual: string = 'Cargando...'; 

  constructor(
    private fb: FormBuilder, 
    private usuarioService: UsuarioService,
    private empleadoService: EmpleadoService,
    private authService: AuthService, 
    private cdr: ChangeDetectorRef 
  ) {
    this.passwordForm = this.fb.group({
      passwordActual: ['', Validators.required],
      passwordNueva: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit() {
    this.rolUsuario = localStorage.getItem('rol') || 'USUARIO';
    this.correoActual = localStorage.getItem('correo') || '';
    
    const idUser = this.authService.obtenerIdUsuario();
    if (idUser) {
      this.idUsuarioActual = idUser;
    }

    const idEmpleado = this.authService.obtenerIdEmpleado();

    if (idEmpleado) {
      this.empleadoService.obtenerPorId(idEmpleado).subscribe({
        next: (datosEmpleado: any) => {
           this.nombreActual = datosEmpleado.nombres + ' ' + datosEmpleado.apellidos;
           this.cdr.detectChanges(); 
        },
        error: (err: any) => {
          console.error('No se pudo obtener el nombre del empleado', err);
          this.nombreActual = 'Mi Perfil'; 
          this.cdr.detectChanges(); 
        }
      });
    } else {
       // Por si el usuario no es empleado (es estudiante)
       this.nombreActual = 'Mi Perfil'; 
    }
  }

  cambiarPassword() {
    if (this.passwordForm.invalid) return;
    
    const { passwordActual, passwordNueva } = this.passwordForm.value;

    this.usuarioService.cambiarPassword(this.idUsuarioActual, passwordActual, passwordNueva).subscribe({
      next: () => {
        alert('Contraseña actualizada exitosamente.');
        this.passwordForm.reset();
      },
      error: (err) => alert('Error: ' + (err.error?.error || 'Revisa tu contraseña actual e intenta de nuevo.'))
    });
  }
}