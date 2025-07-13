import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { ResultService } from '../../services/result.service';
import { EventService } from '../../services/event.service';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { WebSocketService } from '../../services/web-socket.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { Athlete } from '../../models/athlete.model';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  let athleteServiceSpy: jasmine.SpyObj<AthleteService>;
  let coachServiceSpy: jasmine.SpyObj<CoachService>;
  let resultServiceSpy: jasmine.SpyObj<ResultService>;
  let eventServiceSpy: jasmine.SpyObj<EventService>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let webSocketServiceSpy: jasmine.SpyObj<WebSocketService>;

  beforeEach(async () => {
    athleteServiceSpy = jasmine.createSpyObj('AthleteService', ['getById', 'getAll', 'update', 'delete']);
    coachServiceSpy = jasmine.createSpyObj('CoachService', ['getById', 'delete']);
    resultServiceSpy = jasmine.createSpyObj('ResultService', ['getByAthleteId', 'getPdfHistory', 'solicitarGeneracionPdf']);
    eventServiceSpy = jasmine.createSpyObj('EventService', ['getById']);
    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getById']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['isAdmin', 'isAuthenticated', 'logout']);
    webSocketServiceSpy = jasmine.createSpyObj('WebSocketService', ['escucharConfirmacionPdf']);

    await TestBed.configureTestingModule({
      imports: [
        ProfileComponent, // ✅ standalone component importado correctamente
        ReactiveFormsModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { params: { type: 'athlete', id: '123' } } } },
        { provide: AthleteService, useValue: athleteServiceSpy },
        { provide: CoachService, useValue: coachServiceSpy },
        { provide: ResultService, useValue: resultServiceSpy },
        { provide: EventService, useValue: eventServiceSpy },
        { provide: DisciplineService, useValue: disciplineServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: WebSocketService, useValue: webSocketServiceSpy },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();

    authServiceSpy.isAdmin.and.returnValue(true);
    authServiceSpy.isAuthenticated.and.returnValue(true);
    // Para evitar error al subscribirse en ngOnInit
    Object.defineProperty(authServiceSpy, 'user', {
      get: () => of({})
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;

    athleteServiceSpy.getById.and.returnValue(of({
      firstName: 'Juan',
      lastName: 'Pérez',
      licenseNumber: 'LIC123',
      birthDate: '2000-01-01',
      disciplines: [],
    }));

    resultServiceSpy.getByAthleteId.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));

    resultServiceSpy.getPdfHistory.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 5,
      number: 0
    }));

    athleteServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));

    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debe activar modo edición si es admin', () => {
    component.enableEdit();
    expect(component.isEditing).toBeTrue();
    expect(component.profileForm).toBeTruthy();
  });

  it('no debe guardar perfil si el formulario es inválido', () => {
    component.enableEdit();
    component.profileForm.patchValue({ firstName: '', lastName: '', licenseNumber: '' });
    component.saveProfile();
    expect(athleteServiceSpy.update).not.toHaveBeenCalled();
  });

  it('debe guardar perfil si el formulario es válido', fakeAsync(() => {
    component.enableEdit();
    component.profileForm.patchValue({
      firstName: 'Juan',
      lastName: 'Pérez',
      licenseNumber: 'LIC123',
      birthDate: '2000-01-01',
      disciplines: []
    });

    athleteServiceSpy.update.and.returnValue(of({} as Athlete));

    component.saveProfile();
    tick();

    expect(athleteServiceSpy.update).toHaveBeenCalledWith('LIC123', jasmine.any(Object));
    expect(component.isEditing).toBeFalse();
  }));

  it('debe eliminar el perfil si se confirma', () => {
    spyOn(window, 'confirm').and.returnValue(true);
    athleteServiceSpy.delete.and.returnValue(of(void 0));

    component.profile = { licenseNumber: 'LIC123' } as any;
    component.deleteProfile();

    expect(athleteServiceSpy.delete).toHaveBeenCalledWith('LIC123');
  });

  it('no debe eliminar el perfil si se cancela', () => {
    spyOn(window, 'confirm').and.returnValue(false);
    component.profile = { licenseNumber: 'LIC123' } as any;

    component.deleteProfile();

    expect(athleteServiceSpy.delete).not.toHaveBeenCalled();
  });

  it('debe mostrar fecha formateada del atleta', () => {
    component.profile = { birthDate: '2000-01-01' } as any;
    component.isAthlete = true;
    expect(component.formattedBirthDate).toBe('2000-01-01');
  });
});
