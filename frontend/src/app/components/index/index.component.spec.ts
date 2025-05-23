import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IndexComponent } from './index.component';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NgIf } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterTestingModule } from '@angular/router/testing';

describe('IndexComponent', () => {
  let component: IndexComponent;
  let fixture: ComponentFixture<IndexComponent>;
  let authSubject: BehaviorSubject<any | null>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: Partial<Router> & {
    events: any;
    createUrlTree: jasmine.Spy;
    serializeUrl: jasmine.Spy;
  };

  beforeEach(waitForAsync(() => {
    authSubject = new BehaviorSubject<any | null>(null);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], {
      user: authSubject.asObservable()
    });
    authServiceSpy.isAuthenticated.and.callFake(() => authSubject.value != null);

    routerSpy = {
      navigate: jasmine.createSpy('navigate'),
      events: EMPTY,
      createUrlTree: jasmine.createSpy('createUrlTree').and.returnValue([]),
      serializeUrl: jasmine.createSpy('serializeUrl').and.returnValue('')
    };

    TestBed.configureTestingModule({
      imports: [
        IndexComponent,
        NgIf,
        MatToolbarModule,
        MatButtonModule,
        MatCardModule,
        MatIconModule,
        MatMenuModule,
        NoopAnimationsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: {} },
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('by default should show LogIn button only', () => {
    const buttons = fixture.debugElement.queryAll(By.css('.LogSection button'));
    expect(buttons.length).toBe(1);
    expect((buttons[0].nativeElement as HTMLButtonElement).textContent).toContain('LogIn');
  });

  it('should call router.navigate on login()', () => {
    const btn = fixture.debugElement.query(By.css('.LogSection button')).nativeElement as HTMLButtonElement;
    btn.click();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('when authService emits a user, should show LogOut button', () => {
    authSubject.next({ name: 'test' });
    fixture.detectChanges();
    const buttons = fixture.debugElement.queryAll(By.css('.LogSection button'));
    expect(buttons.length).toBe(1);
    expect((buttons[0].nativeElement as HTMLButtonElement).textContent).toContain('LogOut');
  });

  it('should call authService.logout on logout()', () => {
    authSubject.next({ name: 'test' });
    fixture.detectChanges();
    const btn = fixture.debugElement.query(By.css('.LogSection button')).nativeElement as HTMLButtonElement;
    btn.click();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
