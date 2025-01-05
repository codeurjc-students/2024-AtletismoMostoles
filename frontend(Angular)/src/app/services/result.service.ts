import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private apiUrl = 'http://localhost:8080/api/results';

  constructor(private http: HttpClient) {}

  // Get results with pagination and optional filters
  getAll(page: number = 0, size: number = 10, sortBy: string = 'date', eventId?: number, disciplineId?: number): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    if (eventId) {
      params = params.set('eventId', eventId.toString());
    }

    if (disciplineId) {
      params = params.set('disciplineId', disciplineId.toString());
    }

    return this.http.get<any>(this.apiUrl, { params });
  }

  // Get result by ID
  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Create a new result
  create(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  // Update a result by ID
  update(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data);
  }

  // Delete a result by ID
  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
