import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Results } from '../models/results.model';
import { Page } from '../models/page.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class ResultService {
  private apiUrl = '/api/results';

  constructor(private http: HttpClient, private router: Router) {}

  getAll(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'date',
    eventId?: number,
    disciplineId?: number
  ): Observable<Page<Results>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    if (eventId) params = params.set('eventId', eventId.toString());
    if (disciplineId) params = params.set('disciplineId', disciplineId.toString());

    return this.http
      .get<Page<Results>>(this.apiUrl, { params, withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getById(id: number): Observable<Results> {
    return this.http
      .get<Results>(`${this.apiUrl}/${id}`, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  create(data: Results): Observable<Results> {
    return this.http
      .post<Results>(this.apiUrl, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  update(id: number, data: Results): Observable<Results> {
    return this.http
      .put<Results>(`${this.apiUrl}/${id}`, data, { withCredentials: true })
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
  createMultiple(results: any[]): Observable<Results[]> {
    return this.http
      .post<Results[]>(`${this.apiUrl}/batch`, results, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

}
