import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DisciplineDetailsComponent } from './discipline-details.component';
import { ActivatedRoute, Router } from '@angular/router';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('DisciplineDetailsComponent', () => {
  let component: DisciplineDetailsComponent;
  let fixture: ComponentFixture<DisciplineDetailsComponent>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    const routeStub = {
      snapshot: { paramMap: { get: () => '1' } }
    };

    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getById', 'update']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [DisciplineDetailsComponent, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: DisciplineService, useValue: disciplineServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DisciplineDetailsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load discipline and patch form', () => {
    const mockDiscipline = {
      id: 1,
      name: 'Salto',
      description: 'Salto de altura',
      equipment: [],
      coaches: [],
      imageLink: 'link.jpg'
    };
    disciplineServiceSpy.getById.and.returnValue(of(mockDiscipline));

    component.loadDiscipline();

    expect(disciplineServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.discipline).toEqual(mockDiscipline);
    expect(component.disciplineForm.value.name).toBe('Salto');
    expect(component.disciplineForm.value.description).toBe('Salto de altura');
  });

  it('should toggle edit mode', () => {
    expect(component.isEditMode).toBeFalse();
    component.toggleEditMode();
    expect(component.isEditMode).toBeTrue();
  });

  it('should save discipline if form is valid', () => {
    const mockUpdatedDiscipline = {
      id: 1,
      name: 'Nuevo nombre',
      description: 'Nueva descripción',
      equipment: [],
      coaches: []
    };
    component.discipline = {
      id: 1,
      name: 'Salto',
      description: 'Salto de altura',
      equipment: [],
      coaches: []
    };
    component.disciplineForm.setValue({
      name: 'Nuevo nombre',
      description: 'Nueva descripción'
    });
    disciplineServiceSpy.update.and.returnValue(of(mockUpdatedDiscipline));
    spyOn(window, 'alert');
    spyOn(component, 'loadDiscipline');

    component.saveDiscipline();

    expect(disciplineServiceSpy.update).toHaveBeenCalledWith(1, {
      id: 1,
      name: 'Nuevo nombre',
      description: 'Nueva descripción'
    });
    expect(window.alert).toHaveBeenCalledWith('Disciplina actualizada con éxito');
    expect(component.loadDiscipline).toHaveBeenCalled();
    expect(component.isEditMode).toBeFalse();
  });

  it('should not call update if form is invalid', () => {
    component.disciplineForm.setValue({ name: '', description: '' });
    component.saveDiscipline();
    expect(disciplineServiceSpy.update).not.toHaveBeenCalled();
  });

  it('should decrease page on previousPage', () => {
    component.currentPage = 2;
    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  it('should not go below page 1', () => {
    component.currentPage = 1;
    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  it('should increase page on nextPage', () => {
    component.currentPage = 1;
    component.totalPages = 2;
    component.nextPage();
    expect(component.currentPage).toBe(2);
  });

  it('should not go above totalPages', () => {
    component.currentPage = 2;
    component.totalPages = 2;
    component.nextPage();
    expect(component.currentPage).toBe(2);
  });

  it('should open equipment dialog and reload if saved', () => {
    const afterClosedSpy = jasmine.createSpyObj({ subscribe: (fn: any) => fn('save') });
    dialogSpy.open.and.returnValue({ afterClosed: () => afterClosedSpy } as any);
    spyOn(component, 'loadDiscipline');

    component.discipline = { equipment: [] };
    component.openEquipmentDialog();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(component.loadDiscipline).toHaveBeenCalled();
  });

  it('should navigate to login on login()', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call authService.logout()', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
