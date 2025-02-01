import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Discipline } from '../models/discipline.model';
import { Page } from '../models/page.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class DisciplineService {
  private apiUrl = '/api/disciplines';

  constructor(private http: HttpClient, private router: Router) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'name'): Observable<Page<Discipline>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http
      .get<Page<Discipline>>(this.apiUrl, { params, withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getById(id: number): Observable<Discipline> {
    return this.http
      .get<Discipline>(`${this.apiUrl}/${id}`, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  create(data: Discipline): Observable<Discipline> {
    return this.http
      .post<Discipline>(this.apiUrl, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  update(id: number, data: Discipline): Observable<Discipline> {
    return this.http
      .put<Discipline>(`${this.apiUrl}/${id}`, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true })
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
