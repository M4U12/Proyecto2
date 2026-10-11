import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmpleadoService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/personal';

  constructor(private http: HttpClient) {}

  registrarPersonal(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }


  getEmpleados(pagina: number = 1, limite: number = 10): Observable<any> {
    return this.http.get(`${this.apiUrl}?pagina=${pagina}&limite=${limite}`);
  }


  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado`, { estado: estado });
  }

  modificarEmpleado(id: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, datos);
  }
  
  obtenerPorId(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }
}