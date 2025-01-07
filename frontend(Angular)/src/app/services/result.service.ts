import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Results } from '../models/results.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private apiUrl = 'http://localhost:8080/api/results';

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'date', eventId?: number, disciplineId?: number): Observable<Page<Results>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    if (eventId) params = params.set('eventId', eventId.toString());
    if (disciplineId) params = params.set('disciplineId', disciplineId.toString());

    return this.http.get<Page<Results>>(this.apiUrl, { params }).pipe(
      catchError(err => throwError(() => new Error(`Error fetching results: ${err.message}`)))
    );
  }

  getById(id: number): Observable<Results> {
    return this.http.get<Results>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(`Error fetching result with ID ${id}: ${err.message}`)))
    );
  }

  create(data: Results): Observable<Results> {
    return this.http.post<Results>(this.apiUrl, data).pipe(
      catchError(err => throwError(() => new Error(`Error creating result: ${err.message}`)))
    );
  }

  update(id: number, data: Results): Observable<Results> {
    return this.http.put<Results>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => throwError(() => new Error(`Error updating result with ID ${id}: ${err.message}`)))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(`Error deleting result with ID ${id}: ${err.message}`)))
    );
  }
}
