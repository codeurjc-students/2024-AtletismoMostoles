import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Discipline} from '../models/discipline.model';

@Injectable({
  providedIn: 'root'
})
export class DisciplineService {
  private apiUrl = 'http://localhost:8080/api/disciplines';

  constructor(private http: HttpClient) {}

  // Get disciplines with pagination
  getAll(page: number = 0, size: number = 10, sortBy: string = 'name'): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);

    return this.http.get<any>(this.apiUrl, { params });
  }

  // Get discipline by ID
  getById(id: number): Observable<Discipline> {
    return this.http.get<Discipline>(`${this.apiUrl}/${id}`);
  }

  // Create a new discipline
  create(data: Discipline): Observable<Discipline> {
    return this.http.post<Discipline>(this.apiUrl, data);
  }

  // Update a discipline by ID
  update(id: number, data: Discipline): Observable<Discipline> {
    return this.http.put<Discipline>(`${this.apiUrl}/${id}`, data);
  }

  // Delete a discipline by ID
  delete(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
