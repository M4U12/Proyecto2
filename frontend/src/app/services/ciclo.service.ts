import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CicloService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/ciclos'; 

  constructor(private http: HttpClient) { }

  getCiclos(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  crearCiclo(ciclo: any): Observable<any> {
    return this.http.post(this.apiUrl, ciclo);
  }

  abrirCiclo(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/abrir`, {});
  }

  cerrarCiclo(id: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/cerrar`, {});
  }
}