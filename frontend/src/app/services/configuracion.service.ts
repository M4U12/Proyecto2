import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/configuraciones';

  constructor(private http: HttpClient) {}

  listarConfiguraciones(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  actualizarConfiguracion(clave: string, valor: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/${clave}`, { valor });
  }
}