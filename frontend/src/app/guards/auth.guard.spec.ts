import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { of, isObservable } from 'rxjs';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const authServiceMock = jasmine.createSpyObj('AuthService', ['user', 'isAdmin']);
    const routerMock = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    });

    guard = TestBed.inject(AuthGuard);
    authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access if user is authenticated and no ADMIN role required', (done) => {
    authServiceSpy.user = of({ id: 1, name: 'Test User' }) as any;
    authServiceSpy.isAdmin.and.returnValue(false);

    const routeMock: any = { data: {} };
    const stateMock: any = {};

    const result = guard.canActivate(routeMock, stateMock);

    if (isObservable(result)) {
      result.subscribe(res => {
        expect(res).toBeTrue();
        done();
      });
    } else {
      expect(result).toBeTrue();
      done();
    }
  });

  it('should deny access and navigate to login if user is not authenticated', (done) => {
    authServiceSpy.user = of(null) as any;

    const routeMock: any = { data: {} };
    const stateMock: any = { url: '/protected' };

    const result = guard.canActivate(routeMock, stateMock);

    if (isObservable(result)) {
      result.subscribe(res => {
        expect(res).toBeFalse();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { returnUrl: '/protected' } });
        done();
      });
    } else {
      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], { queryParams: { returnUrl: '/protected' } });
      done();
    }
  });

  it('should deny access to ADMIN route if user is not admin', (done) => {
    authServiceSpy.user = of({ id: 1, name: 'Test User' }) as any;
    authServiceSpy.isAdmin.and.returnValue(false);

    const routeMock: any = { data: { roles: ['ADMIN'] } };
    const stateMock: any = {};

    const result = guard.canActivate(routeMock, stateMock);

    if (isObservable(result)) {
      result.subscribe(res => {
        expect(res).toBeFalse();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/access-denied']);
        done();
      });
    } else {
      expect(result).toBeFalse();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/access-denied']);
      done();
    }
  });
});
