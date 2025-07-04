import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Results } from '../models/results.model';
import { Page } from '../models/page.model';
import { Router } from '@angular/router';
import { PdfHistory } from '../models/pdf-history.model';

@Injectable({
  providedIn: 'root',
})
export class ResultService {
  private apiUrl = '/api/results';

  constructor(private http: HttpClient, private router: Router) {}

  getAll(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<Page<Results>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy);

    return this.http
      .get<Page<Results>>(this.apiUrl, { params, withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getById(id: number): Observable<Results> {
    return this.http
      .get<Results>(`${this.apiUrl}/${id}`, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getByAthleteId(
    atletaId: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<Page<Results>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy);

    return this.http
      .get<Page<Results>>(`${this.apiUrl}/athlete/${atletaId}`, {
        params,
        withCredentials: true,
      })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getByEventId(
    eventId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'id'
  ): Observable<Page<Results>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy);

    return this.http
      .get<Page<Results>>(`${this.apiUrl}/event/${eventId}`, {
        params,
        withCredentials: true,
      })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  getPdfHistory(
    atletaId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'timestampGenerado'
  ): Observable<Page<PdfHistory>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy);

    return this.http
      .get<Page<PdfHistory>>(`${this.apiUrl}/pdf/history/${atletaId}`, {
        params,
        withCredentials: true,
      })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  solicitarGeneracionPdf(atletaId: string): Observable<void> {
    return this.http
      .post<void>(`${this.apiUrl}/pdf/${atletaId}`, {}, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  create(data: Results): Observable<Results> {
    return this.http
      .post<Results>(this.apiUrl, data, { withCredentials: true })
      .pipe(catchError((err) => this.handleAuthError(err)));
  }

  createMultiple(results: Results[]): Observable<Results[]> {
    return this.http
      .post<Results[]>(`${this.apiUrl}/batch`, results, { withCredentials: true })
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
      this.router.navigate(['/login'], {
        queryParams: { returnUrl: this.router.url },
      });
    }
    return throwError(() => error);
  }
}
