import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LibroService {
  private apiUrl = 'http://localhost:8080/Proyecto2_coden/api/libros';

  constructor(private http: HttpClient) {}

  registrarLibro(datos: any): Observable<any> {
    return this.http.post(this.apiUrl, datos);
  }

  modificarLibro(id: number, datos: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, datos);
  }

  cambiarEstado(id: number, estado: boolean): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/estado`, { estado });
  }

  listarLibros(pagina: number = 1, limite: number = 10, filtro: string = ''): Observable<any> {
    let url = `${this.apiUrl}?pagina=${pagina}&limite=${limite}`;
    if (filtro) {
      url += `&filtro=${encodeURIComponent(filtro)}`;
    }
    return this.http.get(url);
  }
}