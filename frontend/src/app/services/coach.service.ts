import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Coach } from '../models/coach.model';
import { Page } from '../models/page.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class CoachService {
  private apiUrl = '/api/coaches';

  constructor(private http: HttpClient, private router: Router) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'lastName'): Observable<Page<Coach>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http
      .get<Page<Coach>>(this.apiUrl, { params, withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getById(id: string): Observable<Coach> {
    return this.http
      .get<Coach>(`${this.apiUrl}/${id}`, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  create(data: Coach): Observable<Coach> {
    return this.http
      .post<Coach>(this.apiUrl, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  update(licenseNumber: string, data: Coach): Observable<Coach> {
    return this.http
      .put<Coach>(`${this.apiUrl}/${licenseNumber}`, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  delete(licenseNumber: string): Observable<void> {
    return this.http
      .delete<void>(`${this.apiUrl}/${licenseNumber}`, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
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
    if (filters.licenseNumber) {
      params = params.set('licenseNumber', filters.licenseNumber);
    }
    if (filters.discipline) {
      params = params.set('discipline', filters.discipline);
    }

    return this.http
      .get<Page<Coach>>(`${this.apiUrl}/filter`, { params, withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  private handleAuthError(error: any): Observable<never> {
    if (error.status === 401 || error.status === 403) {
      // Redirigir al login si no está autorizado
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url },
      });
    }
    return throwError(() => error);
  }
}
