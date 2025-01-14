import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Equipment } from '../models/equipment.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class EquipmentService {
  private apiUrl = '/api/equipment';

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 10, sortBy: string = 'name'): Observable<Page<Equipment>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<Page<Equipment>>(this.apiUrl, { params }).pipe(
      catchError(err => {
        console.error('Error fetching equipment', err);
        return throwError(err);
      })
    );
  }

  getById(id: number): Observable<Equipment> {
    return this.http.get<Equipment>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error fetching equipment with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  create(data: Equipment): Observable<Equipment> {
    return this.http.post<Equipment>(this.apiUrl, data).pipe(
      catchError(err => {
        console.error('Error creating equipment', err);
        return throwError(err);
      })
    );
  }

  update(id: number, data: Equipment): Observable<Equipment> {
    return this.http.put<Equipment>(`${this.apiUrl}/${id}`, data).pipe(
      catchError(err => {
        console.error(`Error updating equipment with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => {
        console.error(`Error deleting equipment with ID: ${id}`, err);
        return throwError(err);
      })
    );
  }
}
