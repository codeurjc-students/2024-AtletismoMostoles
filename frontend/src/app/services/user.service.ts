import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) {}

  registerUser(data: { name: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data, { withCredentials: true });
  }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`/api/admin/user/${id}`, { withCredentials: true });
  }

  updateUser(id: number, data: any): Observable<any> {
    return this.http.put(`/api/admin/user/${id}`, data, { withCredentials: true });
  }

  getUsersAdmin(): Observable<any> {
    return this.http.get('/api/admin/users', { withCredentials: true });
  }

}
