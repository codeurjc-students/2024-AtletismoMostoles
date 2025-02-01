import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | boolean {
    return this.authService.user.pipe(
      map(user => {
        const isAuthenticated = !!user; // Usuario autenticado (USER o ADMIN)
        const isAdminRoute = route.data['roles']?.includes('ADMIN');

        if (isAuthenticated) {
          // Si la ruta requiere ADMIN y el usuario no lo es, denegar acceso
          if (isAdminRoute && !this.authService.isAdmin()) {
            this.router.navigate(['/access-denied']);
            return false;
          }
          return true;
        }

        // Usuario no autenticado → Redirigir al login
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
      })
    );
  }
}
