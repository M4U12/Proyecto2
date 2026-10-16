import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AsignacionService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/asignaciones-docentes';

  constructor(private http: HttpClient) {}

  registrarAsignacion(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  listarAsignacionesPorMaestro(idEmpleado: number, idCiclo: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/maestro/${idEmpleado}/ciclo/${idCiclo}`);
  }

  eliminarAsignacion(idAsignacion: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${idAsignacion}`);
  }

  reasignarTitular(idAsignacion: number, idNuevoEmpleado: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idAsignacion}/titular/${idNuevoEmpleado}`, {});
  }
}