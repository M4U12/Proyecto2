import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EstudianteService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/estudiantes';

  constructor(private http: HttpClient) {}

  registrarEstudiante(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  modificarEstudiante(codigo: string, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${codigo}`, datos);
  }

  cambiarEstado(codigo: string, estado: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${codigo}/estado/${estado}`, {});
  }

  obtenerExpediente(codigo: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${codigo}`);
  }
  
  buscarPorNombre(nombre: String): Observable<any> {
    return this.http.get(`${this.apiUrl}/buscar?nombre=${nombre}`);
  }
}