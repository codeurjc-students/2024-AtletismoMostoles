import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Coach } from '../models/coach.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class CoachService {
  private apiUrl = '/api/coaches';

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'lastName'): Observable<Page<Coach>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<Page<Coach>>(this.apiUrl, { params }).pipe(
      catchError(err => {
        console.error('Error fetching coaches', err);
        return throwError(err);
      })
    );
  }

  getById(id: string): Observable<Coach> {
    return this.http.get<Coach>(`${this.apiUrl}/${id}`);
  }

  create(data: Coach): Observable<Coach> {
    return this.http.post<Coach>(this.apiUrl, data).pipe(
      catchError(err => {
        console.error('Error creating coach', err);
        return throwError(err);
      })
    );
  }

  update(licenseNumber: string, data: Coach): Observable<Coach> {
    return this.http.put<Coach>(`${this.apiUrl}/${licenseNumber}`, data).pipe(
      catchError(err => {
        console.error(`Error updating coach with ID: ${licenseNumber}`, err);
        return throwError(err);
      })
    );
  }

  delete(licenseNumber: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${licenseNumber}`).pipe(
      catchError(err => {
        console.error(`Error deleting coach with ID: ${licenseNumber}`, err);
        return throwError(err);
      })
    );
  }

  getFiltered(filters: any, page: number = 0, size: number = 10): Observable<Page<Coach>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters.nombre) {
      params = params.set('firstName', filters.nombre);
    }
    if (filters.apellido) {
      params = params.set('lastName', filters.apellido);
    }
    if (filters.numeroLicencia) {
      params = params.set('licenseNumber', filters.numeroLicencia);
    }
    if (filters.disciplina) {
      params = params.set('discipline', filters.disciplina);
    }

    return this.http.get<Page<Coach>>(`${this.apiUrl}/filter`, { params }).pipe(
      catchError(err => {
        console.error('Error fetching filtered coaches', err);
        return throwError(err);
      })
    );
  }

}
