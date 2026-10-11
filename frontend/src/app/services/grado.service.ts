import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GradoService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/grados';

  constructor(private http: HttpClient) {}

  registrarGrado(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  getGrados(pagina: number = 1, limite: number = 5): Observable<any> {
    return this.http.get(`${this.apiUrl}?pagina=${pagina}&limite=${limite}`);
  }

  modificarGrado(id: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, datos);
  }
}