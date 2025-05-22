import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DisciplineDetailsComponent } from './discipline-details.component';
import { DisciplineService } from '../../services/discipline.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

describe('DisciplineDetailsComponent', () => {
  let component: DisciplineDetailsComponent;
  let fixture: ComponentFixture<DisciplineDetailsComponent>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let alertSpy: jasmine.Spy;

  beforeEach(async () => {
    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getById', 'update']);
    authServiceSpy       = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    routerSpy            = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy            = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [
        DisciplineDetailsComponent,
        HttpClientTestingModule,
        ReactiveFormsModule,
        RouterTestingModule
      ],
      providers: [
        { provide: DisciplineService, useValue: disciplineServiceSpy },
        { provide: AuthService,      useValue: authServiceSpy      },
        { provide: Router,           useValue: routerSpy           },
        { provide: ActivatedRoute,   useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    })
      .overrideProvider(MatDialog, { useValue: dialogSpy })
      .compileComponents();

    TestBed.overrideComponent(DisciplineDetailsComponent, {
      set: { template: '' }
    });

    fixture   = TestBed.createComponent(DisciplineDetailsComponent);
    component = fixture.componentInstance;

    disciplineServiceSpy.getById.and.returnValue(of({
      id: 1,
      name: 'Salto',
      description: 'Prueba',
      equipment: []
    }));
    alertSpy = spyOn(window, 'alert').and.stub();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load discipline on init', () => {
    fixture.detectChanges();
    expect(disciplineServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.discipline).toEqual(jasmine.objectContaining({
      id: 1,
      name: 'Salto',
      description: 'Prueba',
      equipment: []
    }));
  });

  it('should logout and call authService.logout', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should save discipline if form is valid', () => {
    component.discipline = {
      id: 1,
      name: 'Salto',
      description: 'Prueba',
      equipment: []
    };
    component.disciplineForm.setValue({ name: 'Nuevo nombre', description: 'Nueva descripción' });
    disciplineServiceSpy.update.and.returnValue(of(component.discipline));

    component.saveDiscipline();

    expect(disciplineServiceSpy.update).toHaveBeenCalledWith(1, jasmine.objectContaining({
      name: 'Nuevo nombre',
      description: 'Nueva descripción'
    }));
    expect(alertSpy).toHaveBeenCalledWith('Disciplina actualizada con éxito');
  });

  it('should not save if form is invalid', () => {
    component.disciplineForm.setValue({ name: '', description: '' });

    component.saveDiscipline();

    expect(alertSpy).toHaveBeenCalledWith('Por favor, completa el formulario correctamente.');
    expect(disciplineServiceSpy.update).not.toHaveBeenCalled();
  });

  it('should open equipment dialog and reload if saved', () => {
    component.discipline = {
      id: 1,
      name: 'X',
      description: 'Y',
      equipment: []
    };

    fixture.detectChanges();

    dialogSpy.open.and.returnValue({
      afterClosed: () => of({ name: 'Equipo A', type: 'Zapatillas' })
    } as any);

    disciplineServiceSpy.getById.and.returnValue(of({
      id: 1,
      name: 'X',
      description: 'Y',
      equipment: []
    }));

    component.openEquipmentDialog();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(disciplineServiceSpy.getById).toHaveBeenCalledWith(1);
  });

  it('should not reload if dialog is closed without saving', () => {
    component.discipline = {
      id: 1,
      name: 'X',
      description: 'Y',
      equipment: []
    };

    fixture.detectChanges();

    dialogSpy.open.and.returnValue({
      afterClosed: () => of(null)
    } as any);

    // Reset call history from initial load
    disciplineServiceSpy.getById.calls.reset();

    component.openEquipmentDialog();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(disciplineServiceSpy.getById).not.toHaveBeenCalled();
  });
});
