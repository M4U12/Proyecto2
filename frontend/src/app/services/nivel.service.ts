import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NivelService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/niveles';

  constructor(private http: HttpClient) {}

  getNiveles(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  modificarNivel(id: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, datos);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado`, { estado: estado });
  }
}