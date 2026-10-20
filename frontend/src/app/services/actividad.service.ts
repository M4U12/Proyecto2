import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ActividadService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/actividades';

  constructor(private http: HttpClient) {}

  listarActividades(idAsignacion: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/asignacion/${idAsignacion}`);
  }

  crearActividad(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  modificarActividad(idActividad: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idActividad}`, datos);
  }
}