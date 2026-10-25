import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PagoService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/pagos';

  constructor(private http: HttpClient) {}

  cobrarInscripcion(datos: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/inscripcion`, datos);
  }

  anularFactura(numeroFactura: string, motivo: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/anular/${numeroFactura}`, { motivoAnulacion: motivo });
  }
  
  cobrarPagoGeneral(datos: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/general`, datos);
  }
}