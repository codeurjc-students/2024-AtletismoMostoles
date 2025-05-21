import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { APP_BASE_HREF } from '@angular/common';

describe('LoginComponent (unit + integration)', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let authService: AuthService;
  let router: Router;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        ReactiveFormsModule,
        HttpClientTestingModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule,
        LoginComponent
      ],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { queryParams: { returnUrl: '/dummy' } } } },
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
    const initReq = httpMock.expectOne('/api/users/me');
    initReq.flush(null);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('unit: should create component', () => {
    expect(component).toBeTruthy();
  });

  it('unit: form invalid when empty', () => {
    expect(component.loginForm.valid).toBeFalse();
  });

  it('unit: isInvalid returns true after touched invalid field', () => {
    const ctrl = component.loginForm.get('username')!;
    ctrl.markAsTouched();
    fixture.detectChanges();
    expect(component.isInvalid('username')).toBeTrue();
  });

  it('unit: onSubmit with invalid form shows errorMessage', () => {
    component.onSubmit();
    expect(component.errorMessage).toBe('Por favor completa el formulario correctamente.');
  });

  it('unit: logout() calls AuthService.logout', () => {
    const logoutSpy = spyOn(authService, 'logout');
    component.logout();
    expect(logoutSpy).toHaveBeenCalled();
  });

  it('unit: login() navigates to /login', () => {
    component.login();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('int: should send HTTP POST on valid submit and navigate on success', fakeAsync(() => {
    component.loginForm.setValue({ username: 'juan', password: 'secret' });
    component.onSubmit();

    const reqLogin = httpMock.expectOne('/api/auth/login');
    expect(reqLogin.request.method).toBe('POST');
    expect(reqLogin.request.body).toEqual({ username: 'juan', password: 'secret' });
    reqLogin.flush({});
    tick();

    const reqMe = httpMock.expectOne('/api/users/me');
    reqMe.flush(null);
    tick();

    expect(router.navigate).toHaveBeenCalledWith(['/dummy']);
    expect(component.errorMessage).toBe('');
  }));

  it('int: should display errorMessage on HTTP 401', fakeAsync(() => {
    component.loginForm.setValue({ username: 'juan', password: 'wrong' });
    component.onSubmit();

    const reqLogin = httpMock.expectOne('/api/auth/login');
    reqLogin.flush({}, { status: 401, statusText: 'Unauthorized' });
    tick();

    expect(component.errorMessage).toBe('Credenciales incorrectas. Intente nuevamente.');
  }));

  it('integration: template shows errorMessage text', () => {
    component.errorMessage = '¡Error!';
    fixture.detectChanges();
    const el = fixture.debugElement.query(By.css('.error-message'));
    expect(el).not.toBeNull();
    expect(el.nativeElement.textContent).toContain('¡Error!');
  });
});
