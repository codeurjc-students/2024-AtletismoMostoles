import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Discipline } from '../models/discipline.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class DisciplineService {
  private apiUrl = '/api/disciplines';

  constructor(private http: HttpClient) {}

  // Obtener disciplinas con paginación
  getAll(page: number = 0, size: number = 10, sortBy: string = 'name'): Observable<Page<Discipline>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<Page<Discipline>>(this.apiUrl, { params }).pipe(
      catchError(err => {
        console.error('Error fetching disciplines', err);
        return throwError(err);
      })
    );
  }

  // Obtener una disciplina por ID
  getById(id: number): Observable<Discipline> {
    return this.http.get<Discipline>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error fetching discipline with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  // Crear una nueva disciplina
  create(data: Discipline): Observable<Discipline> {
    return this.http.post<Discipline>(this.apiUrl, data).pipe(
      catchError(err => {
        console.error('Error creating discipline', err);
        return throwError(err);
      })
    );
  }

  // Actualizar una disciplina por ID
  update(id: number, data: Discipline): Observable<Discipline> {
    return this.http.put<Discipline>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => {
        console.error(`Error updating discipline with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  // Eliminar una disciplina por ID
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error deleting discipline with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }
}
