import { Component, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth'; 
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {
  loginForm: FormGroup;
  recuperarForm: FormGroup;
  
  mensajeError: string = '';
  modoRecuperacion: boolean = false;
  cargando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.recuperarForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]]
    });
  }

  cambiarModo() {
    this.modoRecuperacion = !this.modoRecuperacion;
    this.mensajeError = '';
    this.loginForm.reset();
    this.recuperarForm.reset();
    this.cdr.detectChanges();
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.cargando = true;
      this.mensajeError = '';
      this.cdr.detectChanges();
      const { correo, password } = this.loginForm.value;
      
      this.authService.login(correo, password).subscribe({
        next: (respuesta) => {
          this.cargando = false;
          alert('¡Bienvenido, ' + respuesta.rol + '!');
          this.router.navigate(['/dashboard']); 
        },
        error: (err) => {
          this.cargando = false;
          this.mensajeError = err.error?.error || 'Credenciales incorrectas';
          this.cdr.detectChanges();
        }
      });
    }
  }

  enviarRecuperacion() {
    if (this.recuperarForm.valid) {
      this.cargando = true;
      this.mensajeError = '';
      this.cdr.detectChanges();
      this.authService.recuperarPassword(this.recuperarForm.value.correo).subscribe({
        next: (res) => {
          this.cargando = false;
          alert(res.mensaje || 'Revisa tu bandeja de entrada.');
          this.cambiarModo(); 
        },
        error: (err) => {
          this.cargando = false;
          this.mensajeError = err.error?.error || 'No se pudo enviar el correo.';
          this.cdr.detectChanges();
        }
      });
    }
  }
}