import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/reportes';

  constructor(private http: HttpClient) {}

  descargarReporteSolventes(idGrado: number, idSeccion: number, idCiclo: number, nombreGrado: string, nombreSeccion: string, anioCiclo: string): Observable<Blob> {
    let params = new HttpParams()
      .set('idGrado', idGrado)
      .set('idSeccion', idSeccion)
      .set('idCiclo', idCiclo)
      .set('nombreGrado', nombreGrado)
      .set('nombreSeccion', nombreSeccion)
      .set('anioCiclo', anioCiclo);

    return this.http.get(`${this.apiUrl}/solventes-morosos`, {
      params: params,
      responseType: 'blob' 
    });
  }

  descargarReporteRendimiento(idGrado: number, idSeccion: number, idCiclo: number, nombreGrado: string, identificadorSeccion: string, anioCiclo: string): Observable<Blob> {
    let params = new HttpParams()
      .set('idGrado', idGrado)
      .set('idSeccion', idSeccion)
      .set('idCiclo', idCiclo)
      .set('nombreGrado', nombreGrado)
      .set('identificadorSeccion', identificadorSeccion)
      .set('anioCiclo', anioCiclo);

    return this.http.get(`${this.apiUrl}/rendimiento`, {
      params: params,
      responseType: 'blob' 
    });
  }

  descargarReporteIngresos(fechaInicio: string, fechaFin: string): Observable<Blob> {
    let params = new HttpParams()
      .set('fechaInicio', fechaInicio || '')
      .set('fechaFin', fechaFin || '');

    return this.http.get(`${this.apiUrl}/ingresos`, {
      params: params,
      responseType: 'blob' 
    });
  }

  descargarReporteMora(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/mora`, { responseType: 'blob' });
  }
}