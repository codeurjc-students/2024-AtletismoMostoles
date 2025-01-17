import { Injectable } from '@angular/core';
import { HttpClient, HttpParams} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Athlete } from '../models/athlete.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class AthleteService {
  private apiUrl = '/api/athletes';

  constructor(private https: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'lastName'): Observable<Page<Athlete>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.https.get<Page<Athlete>>(this.apiUrl, { params }).pipe(
      catchError(err => {
        console.error('Error fetching athletes', err);
        return throwError(err);
      })
    );
  }

  getFiltered(filters: any, page: number, size: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filters.firstName) {
      params = params.set('firstName', filters.firstName);
    }
    if (filters.lastName) {
      params = params.set('lastName', filters.lastName);
    }
    if (filters.discipline) {
      params = params.set('discipline', filters.discipline);
    }
    if (filters.licenseNumber) {
      params = params.set('licenseNumber', filters.licenseNumber);
    }
    if (filters.coach) {
      params = params.set('coach', filters.coach);
    }

    return this.https.get<any>(`${this.apiUrl}/filter`, { params });
  }

  getById(id: string): Observable<Athlete> {
    return this.https.get<Athlete>(`${this.apiUrl}/${id}`);
  }


  create(data: Athlete): Observable<Athlete> {
    return this.https.post<Athlete>(this.apiUrl, data).pipe(
      catchError(err => {
        console.error('Error creating athlete', err);
        return throwError(err);
      })
    );
  }


  update(id: string, data: Athlete): Observable<Athlete> {
    return this.https.put<Athlete>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => {
        console.error(`Error updating athlete with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  delete(id: string): Observable<void> {
    return this.https.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error deleting athlete with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }
}
