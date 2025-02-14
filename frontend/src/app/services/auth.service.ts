import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
    this.userSubject = new BehaviorSubject<any | null>(null);
    this.user = this.userSubject.asObservable();

    this.loadAuthenticatedUser(); // Cargar usuario desde backend
  }

  /**
   * Carga el usuario autenticado usando la cookie del backend.
   */
  private loadAuthenticatedUser(): void {
    this.http.get<any>(`/api/users/me`, { withCredentials: true }).subscribe(
      user => {
        console.log('✅ Usuario autenticado desde backend:', user);
        this.userSubject.next(user);
      },
      error => {
        console.warn('⛔ No hay usuario autenticado.');
        this.userSubject.next(null);
      }
    );
  }

  /**
   * Inicia sesión usando la cookie del backend.
   */
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, { username, password }, { withCredentials: true }).pipe(
      map(response => {
        console.log('✅ Login exitoso:', response);
        this.loadAuthenticatedUser(); // Cargar usuario desde backend después del login
        return response;
      })
    );
  }

  /**
   * Cierra sesión eliminando la cookie del backend.
   */
  logout(): void {
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe(() => {
      this.userSubject.next(null);
      this.router.navigate(['/login']);
    });
  }

  /**
   * Verifica si el usuario está autenticado.
   */
  isAuthenticated(): boolean {
    return !!this.userSubject.value;
  }

  /**
   * Retorna los datos del usuario actual.
   */
  getCurrentUser(): any | null {
    return this.userSubject.value;
  }

  /**
   * Verifica si el usuario es administrador.
   */
  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user && user.roles.includes('ADMIN');
  }
}
