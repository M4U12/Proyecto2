import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SeccionService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/secciones';

  constructor(private http: HttpClient) {}

  // obtener secciones de un grado especifico
  getSeccionesPorGrado(idGrado: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/grado/${idGrado}`);
  }

  agregarSeccion(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  eliminarSeccion(idSeccion: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${idSeccion}`);
  }
}