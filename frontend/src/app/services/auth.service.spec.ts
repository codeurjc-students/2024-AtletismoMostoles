import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpyObj }]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;

    // ✅ Interceptamos la petición automática a /api/users/me al iniciar el servicio
    const initReq = httpMock.expectOne('/api/users/me');
    initReq.flush(null);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should perform login and load user', () => {
    const mockResponse = { token: '123' };
    const mockUser = { username: 'admin', roles: ['ADMIN'] };

    service.login('admin', 'password').subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const loginReq = httpMock.expectOne('/api/auth/login');
    expect(loginReq.request.method).toBe('POST');
    loginReq.flush(mockResponse);

    const userReq = httpMock.expectOne('/api/users/me');
    expect(userReq.request.method).toBe('GET');
    userReq.flush(mockUser);

    service.user.subscribe(user => {
      expect(user).toEqual(mockUser);
    });
  });

  it('should perform logout and navigate to login', () => {
    service.logout();

    const req = httpMock.expectOne('/api/auth/logout');
    expect(req.request.method).toBe('POST');
    req.flush(null);

    expect(service.getCurrentUser()).toBeNull();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should return isAuthenticated true when user is set', () => {
    (service as any).userSubject.next({ username: 'admin' });
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should return isAuthenticated false when no user', () => {
    (service as any).userSubject.next(null);
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should return current user', () => {
    const mockUser = { username: 'john' };
    (service as any).userSubject.next(mockUser);
    expect(service.getCurrentUser()).toEqual(mockUser);
  });

  it('should return true for isAdmin when user is admin', () => {
    const adminUser = { username: 'admin', roles: ['ADMIN'] };
    (service as any).userSubject.next(adminUser);
    expect(service.isAdmin()).toBeTrue();
  });

  it('should return false for isAdmin when user is not admin', () => {
    const normalUser = { username: 'user', roles: ['USER'] };
    (service as any).userSubject.next(normalUser);
    expect(service.isAdmin()).toBeFalse();
  });
});
