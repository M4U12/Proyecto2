import { Component } from '@angular/core';
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
  mensajeError: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      correo: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { correo, password } = this.loginForm.value;
      
      this.authService.login(correo, password).subscribe({
        next: (respuesta) => {
          this.authService.guardarSesion(respuesta);
          alert('¡Bienvenido, ' + respuesta.rol + '!');
          this.router.navigate(['/dashboard']); 
        },
        error: (err) => {
          this.mensajeError = err.error.error || 'Credenciales incorrectas';
        }
      });
    }
  }
}