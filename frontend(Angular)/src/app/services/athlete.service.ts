import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AthleteService {
  private apiUrl = 'http://localhost:8080/api/athletes';

  constructor(private http: HttpClient) {}

  // Get athletes with pagination
  getAll(page: number = 0, size: number = 10, sortBy: string = 'lastName'): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<any>(this.apiUrl, { params });
  }

  // Get athlete by ID
  getById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // Create a new athlete
  create(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  // Update an athlete by ID
  update(id: string, data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, data);
  }

  // Delete an athlete by ID
  delete(id: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
