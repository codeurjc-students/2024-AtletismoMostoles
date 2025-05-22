import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Results } from '../../models/results.model';
import { Page } from '../../models/page.model';
import { By } from '@angular/platform-browser';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { APP_BASE_HREF } from '@angular/common';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;
  let athleteSvc: jasmine.SpyObj<AthleteService>;
  let coachSvc: jasmine.SpyObj<CoachService>;
  let authSvc: jasmine.SpyObj<AuthService>;
  let router: Router;

  const emptyAthletePage: Page<Athlete> = { content: [], totalElements: 0, totalPages: 0, size: 0, number: 0 };
  const emptyCoachPage: Page<Coach> = { content: [], totalElements: 0, totalPages: 0, size: 0, number: 0 };

  beforeEach(async () => {
    athleteSvc = jasmine.createSpyObj('AthleteService', ['getById', 'update', 'delete', 'getAll']);
    coachSvc = jasmine.createSpyObj('CoachService', ['getById', 'update', 'delete', 'getAll']);
    authSvc = jasmine.createSpyObj('AuthService', ['isAdmin', 'isAuthenticated'], { user: of(null) });

    const defaultAthlete: Athlete = {
      licenseNumber: '', firstName: '', lastName: '', birthDate: '',
      disciplines: [], results: [], coach: null
    };
    athleteSvc.getById.and.returnValue(of(defaultAthlete));
    athleteSvc.getAll.and.returnValue(of(emptyAthletePage));
    const defaultCoach: Coach = { /* populate minimally if needed */ } as Coach;
    coachSvc.getById.and.returnValue(of(defaultCoach));
    coachSvc.getAll.and.returnValue(of(emptyCoachPage));

    await TestBed.configureTestingModule({
      imports: [
        ProfileComponent,
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([]),
        MatCardModule,
        MatListModule,
        MatTableModule,
        MatPaginatorModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AthleteService, useValue: athleteSvc },
        { provide: CoachService, useValue: coachSvc },
        { provide: AuthService, useValue: authSvc },
        { provide: ActivatedRoute, useValue: { snapshot: { params: { type: 'athlete', id: 'A100' } } } },
        { provide: APP_BASE_HREF, useValue: '/' }
      ]
    }).compileComponents();

    authSvc.isAuthenticated.and.returnValue(true);
    authSvc.isAdmin.and.returnValue(true);

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('carga perfil de atleta y disciplinas', fakeAsync(() => {
      const fakeAthlete: Athlete = {
        licenseNumber: 'A100',
        firstName: 'Juan',
        lastName: 'Pérez',
        birthDate: '1990-05-05',
        disciplines: [{ id: 1, name: 'Salto', description: 'Longitud' }],
        results: [],
        coach: null
      };
      athleteSvc.getById.and.returnValue(of(fakeAthlete));

      component.ngOnInit();
      tick();
      fixture.detectChanges();

      expect(athleteSvc.getById).toHaveBeenCalledWith('A100');
      expect(component.profile).toEqual(fakeAthlete);
      expect(component.disciplines!).toEqual(fakeAthlete.disciplines!);
      const title = fixture.nativeElement.querySelector('mat-card-title')!.textContent!.trim();
      expect(title).toBe('Juan Pérez');
    }));

    it('muestra mensaje de error si falla carga', fakeAsync(() => {
      athleteSvc.getById.and.returnValue(throwError(() => new Error('fail')));

      component.ngOnInit();
      tick();
      fixture.detectChanges();

      expect(component.errorMessage).toBe('Error al cargar el perfil. Inténtalo de nuevo más tarde.');
      expect(fixture.debugElement.query(By.css('.profile-card'))).toBeTruthy();
    }));
  });

  describe('enableEdit y saveProfile', () => {
    beforeEach(() => {
      component.profile = {
        licenseNumber: 'A100',
        firstName: 'Juan',
        lastName: 'Pérez',
        birthDate: '1990-05-05',
        disciplines: [],
        results: [],
        coach: null
      };
      component.isAdmin = true;
    });

    it('activa edición y arma el formulario', () => {
      component.enableEdit();
      expect(component.isEditing).toBeTrue();
      expect(component.profileForm.value.firstName).toBe('Juan');
      expect(component.profileForm.value.licenseNumber).toBe('A100');
    });

    it('no guarda si formulario inválido', () => {
      component.enableEdit();
      component.profileForm.controls['firstName'].setValue('');
      athleteSvc.update.and.returnValue(of(component.profile as Athlete));
      component.saveProfile();
      expect(athleteSvc.update).not.toHaveBeenCalled();
    });

    it('guarda y recarga tras edición válida', fakeAsync(() => {
      component.enableEdit();
      component.profileForm.patchValue({ firstName: 'Ana', lastName: 'García' });

      const updatedProfile: Athlete = {
        ...(component.profile as Athlete),
        firstName: 'Ana',
        lastName: 'García',
        birthDate: (component.profile as Athlete).birthDate
      };

      athleteSvc.update.and.returnValue(of(updatedProfile));
      athleteSvc.getById.and.returnValue(of(updatedProfile));

      spyOn(window, 'alert').and.stub();

      component.saveProfile();
      tick();
      fixture.detectChanges();

      expect(athleteSvc.update).toHaveBeenCalledWith(
        'A100',
        jasmine.objectContaining({ firstName: 'Ana', lastName: 'García' })
      );
      expect(window.alert).toHaveBeenCalledWith('Perfil actualizado con éxito');
      expect(athleteSvc.getById).toHaveBeenCalledWith('A100');
    }));
  });

  describe('deleteProfile', () => {
    beforeEach(() => {
      component.profile = {
        licenseNumber: 'A100',
        firstName: '',
        lastName: '',
        birthDate: '',
        disciplines: [],
        results: [],
        coach: null
      };
      component.isAdmin = true;
    });

    it('elimina y navega si confirma', fakeAsync(() => {
      spyOn(window, 'confirm').and.returnValue(true);
      athleteSvc.delete.and.returnValue(of(undefined));
      component.deleteProfile();
      tick();
      expect(athleteSvc.delete).toHaveBeenCalledWith('A100');
      expect(router.navigate).toHaveBeenCalledWith(['/ranking']);
    }));

    it('no elimina si cancela', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      athleteSvc.delete.and.returnValue(of(undefined));
      component.deleteProfile();
      expect(athleteSvc.delete).not.toHaveBeenCalled();
    });
  });

  describe('paginación de resultados', () => {
    beforeEach(() => {
      component.isAthlete = true;
      component.results = Array.from({ length: 15 }, (_, i) => ({ id: i, value: i } as Results));
      component.totalPages = Math.ceil(component.results.length / component.itemsPerPage);
      component.updatePagination();
    });

    it('divide los resultados en páginas', () => {
      expect(component.paginatedResults.length).toBe(component.itemsPerPage);
      component.nextPage();
      expect(component.currentPage).toBe(2);
      expect(component.paginatedResults[0].value).toBe(component.itemsPerPage);
    });

    it('prevPage funciona correctamente', () => {
      component.currentPage = 2;
      component.updatePagination();
      component.prevPage();
      expect(component.currentPage).toBe(1);
    });
  });
});
