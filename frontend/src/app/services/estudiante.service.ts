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

  getAlumnosPorClase(idAsignacion: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/asignacion/${idAsignacion}`);
  }

  crearCredenciales(datos: any): Observable<any> {
    return this.http.post('http://localhost:8080/Proyecto2_coden/api/usuarios/estudiante', datos);
  }
  
  getCursosPorEstudiante(idEstudiante: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${idEstudiante}/cursos`);
  }

  obtenerPorId(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/id/${id}`);
  }

  getEstudiantesPaginados(pagina: number = 1, limite: number = 10, filtro: string = ''): Observable<any> {
    let url =  `${this.apiUrl}?pagina=${pagina}&limite=${limite}`;
    if (filtro) {
      // encodeURIComponent protege caracteres especiales como espacios o ñ al enviarlos por URL
      url += `&filtro=${encodeURIComponent(filtro)}`;
    }
    return this.http.get(url);
  }
}