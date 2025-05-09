import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule }     from '@angular/material/input';
import { MatButtonModule }    from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';

describe('LoginComponent (unit + light integration)', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;

  // --- Para tests unitarios ---
  let authStub: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  // --- Para tests de integración ligeros ---
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    authStub = jasmine.createSpyObj('AuthService', ['login', 'logout'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        FormsModule,
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        NoopAnimationsModule
      ],
      declarations: [LoginComponent],
      providers: [
        // Para unit tests, inyectamos el stub; para integration, se sobreescribirá con el real
        { provide: AuthService, useValue: authStub },
        { provide: Router,      useValue: routerSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: { returnUrl: '/dummy' } } }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock  = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    // para tests de integración
    httpMock.verify();
  });

  // -----------------
  // UNIT TESTS
  // -----------------

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
    expect(component.errorMessage)
      .toBe('Por favor completa el formulario correctamente.');
  });

  it('unit: logout() calls AuthService.logout', () => {
    component.logout();
    expect(authStub.logout).toHaveBeenCalled();
  });

  it('unit: login() navigates to /login', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  // -----------------
  // INTEGRATION TESTS
  // -----------------

  it('int: should send HTTP POST on valid submit and navigate on success', fakeAsync(() => {
    // Reemplazamos el stub por el AuthService real (con HttpClientTestingModule)
    const realAuth = TestBed.inject(AuthService);
    spyOn(realAuth, 'login').and.callThrough(); // dejar pasar al HttpClient

    // preparar form
    component.loginForm.setValue({ username: 'juan', password: 'secret' });
    component.onSubmit();

    // esperar la petición HTTP
    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'juan', password: 'secret' });

    // simulamos respuesta exitosa
    req.flush({ /* payload de AuthResponse */ });
    tick();

    expect(realAuth.login).toHaveBeenCalledWith('juan', 'secret');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/dummy']);
    expect(component.errorMessage).toBe('');
  }));

  it('int: should display errorMessage on HTTP 401', fakeAsync(() => {
    const realAuth = TestBed.inject(AuthService);
    spyOn(realAuth, 'login').and.callThrough();

    component.loginForm.setValue({ username: 'juan', password: 'wrong' });
    component.onSubmit();

    const req = httpMock.expectOne('/api/auth/login');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
    tick();

    expect(component.errorMessage)
      .toBe('Credenciales incorrectas. Intente nuevamente.');
  }));

  it('integration: template shows errorMessage text', fakeAsync(() => {
    component.errorMessage = '¡Error!';
    fixture.detectChanges();
    const el = fixture.debugElement.query(By.css('.error-message'));
    expect(el.nativeElement.textContent).toContain('¡Error!');
  }));
});
