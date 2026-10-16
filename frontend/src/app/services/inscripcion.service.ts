import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InscripcionService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/inscripciones';

  constructor(private http: HttpClient) {}

  inscribirEstudiante(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  corregirInscripcion(idEstudiante: number, idCiclo: number, idNuevoGrado: number, idNuevaSeccion: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/estudiante/${idEstudiante}/ciclo/${idCiclo}/grado/${idNuevoGrado}/seccion/${idNuevaSeccion}`, {});
  }
}