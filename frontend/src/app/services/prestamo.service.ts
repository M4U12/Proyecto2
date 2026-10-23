import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PrestamoService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/prestamos';

  constructor(private http: HttpClient) {}

  solicitarPrestamo(datos: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/solicitar`, datos);
  }

  aprobarPrestamo(idPrestamo: number, fechaLimite: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idPrestamo}/aprobar`, { fechaLimite });
  }

  rechazarPrestamo(idPrestamo: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idPrestamo}/rechazar`, {});
  }

  devolverLibro(idPrestamo: number, multa: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idPrestamo}/devolver`, { multa });
  }

  renovarPrestamo(idPrestamo: number, fechaLimite: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${idPrestamo}/renovar`, { fechaLimite });
  }

  listarPrestamos(idEstudiante?: number, idEmpleado?: number, estado?: string, pagina: number = 1, limite: number = 10): Observable<any[]> {
    let params = new HttpParams()
      .set('pagina', pagina.toString())
      .set('limite', limite.toString());
      
    if (idEstudiante) params = params.set('idEstudiante', idEstudiante);
    if (idEmpleado) params = params.set('idEmpleado', idEmpleado);
    if (estado) params = params.set('estado', estado);

    return this.http.get<any[]>(this.apiUrl, { params });
  }
}