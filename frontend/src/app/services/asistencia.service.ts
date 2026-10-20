import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AsistenciaService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/asistencias';

  constructor(private http: HttpClient) {}

  getAsistenciaPorFecha(idAsignacion: number, fecha: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/asignacion/${idAsignacion}/fecha/${fecha}`);
  }

  registrarAsistencia(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  modificarAsistencia(idAsistencia: number, nuevoEstado: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idAsistencia}/estado/${nuevoEstado}`, {});
  }
}