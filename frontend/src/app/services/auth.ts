import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/auth';

  constructor(private http: HttpClient) { }

  // para iniciar sesión
  login(correo: string, password: string): Observable<any> {
    const credenciales = { correo, password };
    return this.http.post(`${this.apiUrl}/login`, credenciales);
  }

  // guarda los datos del usuario en localStorage para saber que esta logueado
  guardarSesion(usuario: any) {
    localStorage.setItem('usuarioLogueado', JSON.stringify(usuario));
  }

  // obtener los datos del usuario logueado
  obtenerSesion() {
    const usuario = localStorage.getItem('usuarioLogueado');
    return usuario ? JSON.parse(usuario) : null;
  }

  //Cerrar sesión
  logout() {
    localStorage.removeItem('usuarioLogueado');
  }
}