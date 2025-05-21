import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssociationAtlComponent } from './association-atl.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { Component } from '@angular/core';
import { provideRouter } from '@angular/router';

@Component({ template: '' })
class DummyComponent {}

describe('AssociationAtlComponent', () => {
  let component: AssociationAtlComponent;
  let fixture: ComponentFixture<AssociationAtlComponent>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(async () => {
    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getAll', 'delete', 'create']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        AssociationAtlComponent,
        HttpClientTestingModule,
        ReactiveFormsModule
      ],
      providers: [
        provideRouter([
          { path: 'discipline-details/:id', component: DummyComponent },
          { path: 'login', component: DummyComponent }
        ]),
        { provide: DisciplineService, useValue: disciplineServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: ActivatedRoute, useValue: {} }
      ]
    }).compileComponents();

    TestBed.overrideComponent(AssociationAtlComponent, {
      set: { template: '' }
    });

    fixture = TestBed.createComponent(AssociationAtlComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load disciplines on init', () => {
    const mockResponse = {
      content: [{
        id: 1,
        name: 'Salto',
        imageLink: 'img.jpg',
        description: 'Salto de altura'
      }],
      totalElements: 1,
      totalPages: 2,
      size: 10,
      number: 0
    };
    disciplineServiceSpy.getAll.and.returnValue(of(mockResponse));

    fixture.detectChanges();

    expect(component.disciplines.length).toBe(1);
    expect(component.totalPages).toBe(2);
  });

  it('should navigate to details', () => {
    component.goToDetails(5);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/discipline-details/5']);
  });

  it('should call previousPage and load data', () => {
    component.currentPage = 2;
    disciplineServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 10,
      number: 1
    }));
    component.previousPage();
    expect(component.currentPage).toBe(1);
    expect(disciplineServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should call nextPage and load data', () => {
    component.currentPage = 1;
    component.totalPages = 2;
    disciplineServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 10,
      number: 2
    }));
    component.nextPage();
    expect(component.currentPage).toBe(2);
    expect(disciplineServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should confirm and delete discipline', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    disciplineServiceSpy.delete.and.returnValue(of(undefined));
    disciplineServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));

    component.confirmDelete(3);

    expect(disciplineServiceSpy.delete).toHaveBeenCalledWith(3);
    expect(disciplineServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should not delete if confirm is cancelled', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.confirmDelete(99);
    expect(disciplineServiceSpy.delete).not.toHaveBeenCalled();
  });

  it('should call logout on authService', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should create discipline when form is valid', () => {
    component.disciplineForm.setValue({
      name: 'Nueva',
      schedule: 'Lunes',
      imageLink: 'link.jpg'
    });
    disciplineServiceSpy.create.and.returnValue(of({
      id: 99,
      name: 'Nueva',
      imageLink: 'link.jpg',
      description: 'Descripción de la nueva disciplina'
    }));
    disciplineServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));

    spyOn(window, 'alert').and.stub();
    component.createDiscipline();

    expect(disciplineServiceSpy.create).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('Disciplina creada exitosamente');
  });

  it('should alert when form is invalid', () => {
    spyOn(window, 'alert').and.stub();
    component.createDiscipline();
    expect(window.alert).toHaveBeenCalledWith('Por favor, complete todos los campos requeridos');
  });
});
