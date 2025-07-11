import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface NotificacionData {
  eventoId: number;
  name: string;
  date: string;
  mapLink: string;
  imageLink: string;
  organizedByClub: boolean;
  timestampNotificacion: string;
  disciplineIds: number[];
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authUrl = '/api/auth';
  private userSubject: BehaviorSubject<any | null>;
  public user: Observable<any | null>;

  // ✅ Nuevas notificaciones recibidas tras login
  public notificacionesPendientes: NotificacionData[] = [];

  constructor(private http: HttpClient, private router: Router) {
    this.userSubject = new BehaviorSubject<any | null>(null);
    this.user = this.userSubject.asObservable();

    this.loadAuthenticatedUser(); // Cargar usuario desde backend
  }

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

  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.authUrl}/login`, { username, password }, { withCredentials: true }).pipe(
      map(response => {
        console.log('✅ Login exitoso:', response);
        this.loadAuthenticatedUser();

        this.notificacionesPendientes = response.notificacionesPendientes || [];

        return response;
      })
    );
  }

  logout(): void {
    this.http.post(`${this.authUrl}/logout`, {}, { withCredentials: true }).subscribe(() => {
      this.userSubject.next(null);
      this.notificacionesPendientes = [];
      this.router.navigate(['/login']);
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
}
