import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClubMembersComponent } from './clubmembers.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('ClubMembersComponent', () => {
  let component: ClubMembersComponent;
  let fixture: ComponentFixture<ClubMembersComponent>;
  let coachServiceSpy: jasmine.SpyObj<CoachService>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    coachServiceSpy = jasmine.createSpyObj('CoachService', ['getAll', 'getFiltered', 'create']);
    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getAll']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated', 'isAdmin'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [ClubMembersComponent, HttpClientTestingModule, ReactiveFormsModule],
      providers: [
        { provide: CoachService, useValue: coachServiceSpy },
        { provide: DisciplineService, useValue: disciplineServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClubMembersComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load coaches', () => {
    const mockResponse = {
      content: [{ licenseNumber: 'C001', firstName: 'John', lastName: 'Doe', disciplines: [] }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };
    coachServiceSpy.getAll.and.returnValue(of(mockResponse));

    component.loadCoaches();

    expect(coachServiceSpy.getAll).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(1);
    expect(component.totalPages).toBe(1);
  });

  it('should load disciplines', () => {
    const mockResponse = {
      content: [{ id: 1, name: 'Salto', description: 'Salto de altura' }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };
    disciplineServiceSpy.getAll.and.returnValue(of(mockResponse));

    component.loadDisciplines();

    expect(disciplineServiceSpy.getAll).toHaveBeenCalled();
    expect(component.disciplines.length).toBe(1);
  });

  it('should apply filters', () => {
    const mockResponse = {
      content: [{ licenseNumber: 'C002', firstName: 'Jane', lastName: 'Smith', disciplines: [] }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };
    coachServiceSpy.getFiltered.and.returnValue(of(mockResponse));

    component.applyFilters();

    expect(coachServiceSpy.getFiltered).toHaveBeenCalled();
    expect(component.dataSource.data.length).toBe(1);
    expect(component.totalPages).toBe(1);
  });

  it('should go to previous page', () => {
    component.currentPage = 2;
    coachServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 10,
      number: 1
    }));

    component.prevPage();
    expect(component.currentPage).toBe(1);
    expect(coachServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should go to next page', () => {
    component.currentPage = 1;
    component.totalPages = 2;
    coachServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 10,
      number: 2
    }));

    component.nextPage();
    expect(component.currentPage).toBe(2);
    expect(coachServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should create coach if form is valid and user is admin', () => {
    component.isAdmin = true;
    component.coachForm.setValue({
      licenseNumber: 'C003',
      firstName: 'Test',
      lastName: 'Coach',
      disciplines: [1]
    });
    coachServiceSpy.create.and.returnValue(of({
      licenseNumber: 'C003',
      firstName: 'Test',
      lastName: 'Coach',
      disciplines: []
    }));
    coachServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));

    spyOn(window, 'alert');
    component.createCoach();

    expect(coachServiceSpy.create).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Entrenador creado exitosamente');
  });

  it('should not create coach if form is invalid', () => {
    component.isAdmin = true;
    spyOn(window, 'alert');
    component.createCoach();
    expect(window.alert).toHaveBeenCalledWith('Por favor, complete todos los campos requeridos');
  });

  it('should not create coach if user is not admin', () => {
    component.isAdmin = false;
    spyOn(window, 'alert');
    component.createCoach();
    expect(window.alert).toHaveBeenCalledWith('Solo los administradores pueden crear entrenadores.');
  });

  it('should navigate to coach profile', () => {
    const coach = { licenseNumber: 'C004' };
    component.goToCoachProfile(coach as any);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile', 'coach', 'C004']);
  });

  it('should call logout on authService', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
