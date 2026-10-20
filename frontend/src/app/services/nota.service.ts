import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotaService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/notas';

  constructor(private http: HttpClient) {}

  listarNotasPorActividad(idActividad: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/actividad/${idActividad}`);
  }

  registrarNota(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  modificarNota(idNota: number, nuevoPunteo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idNota}/punteo/${nuevoPunteo}`, {});
  }

  obtenerBoleta(idEstudiante: number, idAsignacion: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/boleta/estudiante/${idEstudiante}/asignacion/${idAsignacion}`);
  }
}