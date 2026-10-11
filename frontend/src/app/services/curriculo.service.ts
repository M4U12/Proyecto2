import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CurriculoService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/curriculos';

  constructor(private http: HttpClient) {}

  obtenerCursosCurriculo(idGrado: number, idCiclo: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/grado/${idGrado}/ciclo/${idCiclo}`);
  }

  definirCurriculo(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  obtenerMallaCompleta(idCiclo: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/ciclo/${idCiclo}`);
  }
}