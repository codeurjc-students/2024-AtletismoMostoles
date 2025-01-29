import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = '/api/auth';
  private userSubject: BehaviorSubject<any | null>;
  public user: Observable<any | null>;

  constructor(private http: HttpClient, private router: Router) {
    this.userSubject = new BehaviorSubject<any | null>(this.getUserFromToken());
    this.user = this.userSubject.asObservable();
  }


  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, { username, password }, { withCredentials: true }).pipe(
      map(response => {
        const user = this.getUserFromToken();
        this.userSubject.next(user);
        console.log('Usuario autenticado:', user); // 👀 Verificar en consola
        return user;
      })
    );
  }


  logout(): void {
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe(() => {
      this.clearUser();
      this.router.navigate(['/']);
    });
  }

  isAuthenticated(): boolean {
    return !!this.userSubject.value;
  }

  getCurrentUser(): any | null {
    return this.userSubject.value;
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user && user.roles.includes('ADMIN');
  }

  private getUserFromToken(): any | null {
    const token = this.getTokenFromCookie('access_token');
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1])); // Decodificar payload JWT
      return { username: payload.sub, roles: payload.auth || [] };
    } catch (error) {
      console.error('Error decoding JWT:', error);
      return null;
    }
  }

  private getTokenFromCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    return match ? match[2] : null;
  }

  private clearUser(): void {
    this.userSubject.next(null);
  }
}

