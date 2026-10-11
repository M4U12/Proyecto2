import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CarreraService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/carreras';

  constructor(private http: HttpClient) {}

  registrarCarrera(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  getCarreras(pagina: number = 1, limite: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}?pagina=${pagina}&limite=${limite}`);
  }

  modificarCarrera(id: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, datos);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado`, { estado: estado });
  }
}