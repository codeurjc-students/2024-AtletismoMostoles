import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Event } from '../models/event.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private apiUrl = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'date', startDate?: string, endDate?: string): Observable<Page<Event>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<Page<Event>>(this.apiUrl, { params }).pipe(
      catchError(err => throwError(() => new Error(`Error fetching events: ${err.message}`)))
    );
  }

  getById(id: number): Observable<Event> {
    return this.http.get<Event>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(`Error fetching event with ID ${id}: ${err.message}`)))
    );
  }

  create(data: Event): Observable<Event> {
    return this.http.post<Event>(this.apiUrl, data).pipe(
      catchError(err => throwError(() => new Error(`Error creating event: ${err.message}`)))
    );
  }

  update(id: number, data: Event): Observable<Event> {
    return this.http.put<Event>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => throwError(() => new Error(`Error updating event with ID ${id}: ${err.message}`)))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => throwError(() => new Error(`Error deleting event with ID ${id}: ${err.message}`)))
    );
  }
}
