import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpClientModule } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Athlete } from '../models/athlete.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class AthleteService {
  private apiUrl = 'http://localhost:8080/api/athletes';

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'lastName'): Observable<Page<Athlete>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<Page<Athlete>>(this.apiUrl, { params }).pipe(
      catchError(err => {
        console.error('Error fetching athletes', err);
        return throwError(err);
      })
    );
  }

  getById(id: string): Observable<Athlete> {
    return this.http.get<Athlete>(`${this.apiUrl}/${id}`);
  }


  create(data: Athlete): Observable<Athlete> {
    return this.http.post<Athlete>(this.apiUrl, data).pipe(
      catchError(err => {
        console.error('Error creating athlete', err);
        return throwError(err);
      })
    );
  }


  update(id: string, data: Athlete): Observable<Athlete> {
    return this.http.put<Athlete>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => {
        console.error(`Error updating athlete with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error deleting athlete with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }
}
