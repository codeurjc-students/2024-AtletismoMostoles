import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Coach } from '../models/coach.model';

@Injectable({
  providedIn: 'root'
})
export class CoachService {
  private apiUrl = 'http://localhost:8080/api/coaches';

  constructor(private http: HttpClient) {}

  // Obtener todos los entrenadores
  getAll(): Observable<Coach[]> {
    return this.http.get<Coach[]>(this.apiUrl);
  }

  // Obtener un entrenador por ID
  getById(licenseNumber: string): Observable<Coach> {
    return this.http.get<Coach>(`${this.apiUrl}/${licenseNumber}`);
  }

  // Crear un nuevo entrenador
  create(data: Coach): Observable<Coach> {
    return this.http.post<Coach>(this.apiUrl, data);
  }

  // Actualizar un entrenador por ID
  update(licenseNumber: string, data: Coach): Observable<Coach> {
    return this.http.put<Coach>(`${this.apiUrl}/${licenseNumber}`, data);
  }

  // Eliminar un entrenador por ID
  delete(licenseNumber: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${licenseNumber}`);
  }
}
