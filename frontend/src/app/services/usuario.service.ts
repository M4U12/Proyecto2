import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/usuarios';

  constructor(private http: HttpClient) {}

  modificarPerfil(idUsuario: number, correo: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idUsuario}/perfil`, { correo: correo });
  }

  cambiarPassword(idUsuario: number, passwordActual: string, passwordNueva: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idUsuario}/password`, { 
      passwordActual: passwordActual, 
      passwordNueva: passwordNueva 
    });
  }
}