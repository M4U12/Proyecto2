import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/auth';

  constructor(private http: HttpClient, private router: Router) { }

  login(correo: string, password: string): Observable<any> {
    const credenciales = { correo, password };
    return this.http.post(`${this.apiUrl}/login`, credenciales).pipe(
      tap((respuesta: any) => {
        localStorage.setItem('token', respuesta.token);
        localStorage.setItem('rol', respuesta.rol);
        localStorage.setItem('correo', respuesta.correo);
        
        this.guardarSesion(respuesta);
      })
    );
  }

  recuperarPassword(correo: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/recuperar-password`, { correo: correo });
  }

  guardarSesion(usuario: any) {
    localStorage.setItem('usuarioLogueado', JSON.stringify(usuario));
  }

  obtenerSesion() {
    const usuario = localStorage.getItem('usuarioLogueado');
    return usuario ? JSON.parse(usuario) : null;
  }


  logout() {
    localStorage.clear(); 
    this.router.navigate(['/login']);
  }

  private decodificarToken(): any {
    const token = localStorage.getItem('token');
    if (!token) return null;
    
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      console.error('Error al decodificar el token JWT', e);
      return null;
    }
  }

  obtenerIdUsuario(): number | null {
    const payload = this.decodificarToken();
    return payload ? payload.idUsuario : null;
  }

  obtenerIdEmpleado(): number | null {
    const payload = this.decodificarToken();
    return payload ? payload.idEmpleado : null;
  }

  // Para uso futuro
  obtenerIdEstudiante(): number | null {
    const payload = this.decodificarToken();
    return payload ? payload.idEstudiante : null;
  }
}