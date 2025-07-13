import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { EventDetailsComponent } from './event-details.component';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { AthleteService } from '../../services/athlete.service';
import { MatDialog } from '@angular/material/dialog';
import { ResultService } from '../../services/result.service';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import { DisciplineService } from '../../services/discipline.service';

describe('EventDetailsComponent', () => {
  let component: EventDetailsComponent;
  let fixture: ComponentFixture<EventDetailsComponent>;
  let eventServiceSpy: jasmine.SpyObj<EventService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let athleteServiceSpy: jasmine.SpyObj<AthleteService>;
  let disciplineServiceSpy: jasmine.SpyObj<DisciplineService>;
  let resultServiceSpy: jasmine.SpyObj<ResultService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let sanitizerSpy: jasmine.SpyObj<DomSanitizer>;

  beforeEach(async () => {
    const routeStub = { snapshot: { paramMap: { get: () => '1' } } };

    eventServiceSpy = jasmine.createSpyObj('EventService', ['getById', 'update']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    athleteServiceSpy = jasmine.createSpyObj('AthleteService', ['getAll']);
    disciplineServiceSpy = jasmine.createSpyObj('DisciplineService', ['getAll']);
    resultServiceSpy = jasmine.createSpyObj('ResultService', ['createMultiple', 'getByEventId']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    sanitizerSpy = jasmine.createSpyObj('DomSanitizer', ['bypassSecurityTrustResourceUrl']);

    await TestBed.configureTestingModule({
      imports: [EventDetailsComponent, ReactiveFormsModule, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: AthleteService, useValue: athleteServiceSpy },
        { provide: DisciplineService, useValue: disciplineServiceSpy }, // ✅ FALTABA ESTO
        { provide: ResultService, useValue: resultServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: DomSanitizer, useValue: sanitizerSpy }
      ]
    })
      .overrideProvider(MatDialog, { useValue: dialogSpy })
      .compileComponents();

    TestBed.overrideComponent(EventDetailsComponent, {
      set: { template: '' }
    });

    fixture = TestBed.createComponent(EventDetailsComponent);
    component = fixture.componentInstance;
    spyOn(window, 'alert').and.stub();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load event and patch form', () => {
    const mockEvent = {
      id: 1,
      name: 'Maratón',
      date: '2025-10-10',
      organizedByClub: true,
      imageLink: 'link.jpg',
      mapLink: 'maplink',
      creationTime: '2025-01-01T12:00:00',
      results: [],
      disciplines: []
    };

    eventServiceSpy.getById.and.returnValue(of(mockEvent));
    resultServiceSpy.getByEventId.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    }));
    sanitizerSpy.bypassSecurityTrustResourceUrl.and.returnValue('sanitized-url' as any);

    component.loadEvent();

    expect(eventServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.event).toEqual(mockEvent);
    expect(component.eventForm.value).toEqual(jasmine.objectContaining({
      name: 'Maratón',
      date: '2025-10-10',
      isOrganizedByClub: true,
      imageLink: 'link.jpg',
      mapLink: 'maplink'
    }));
    expect(component.sanitizedMapUrl).toBe('sanitized-url' as any);
  });

  it('should open add results dialog and save results', fakeAsync(() => {
    const mockAthletesResponse = {
      content: [
        {
          licenseNumber: 'A001',
          firstName: 'John',
          lastName: 'Doe',
          birthDate: '2000-01-01'
        }
      ],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };

    const mockDisciplinesResponse = {
      content: [
        {
          id: 1,
          name: '100m',
          description: 'Carrera de velocidad'
        }
      ],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };

    const mockResults = [
      {
        valor: '10',
        atletaId: 'A001',
        eventoId: 1,
        disciplinaId: 1
      }
    ];

    athleteServiceSpy.getAll.and.returnValue(of(mockAthletesResponse));
    disciplineServiceSpy.getAll.and.returnValue(of(mockDisciplinesResponse));
    dialogSpy.open.and.returnValue({ afterClosed: () => of(mockResults) } as any);
    resultServiceSpy.createMultiple.and.returnValue(of([]));
    const loadSpy = spyOn(component, 'loadEvent');

    component.event = {
      id: 1,
      name: 'Evento de prueba',
      date: '2025-01-01',
      organizedByClub: true,
      creationTime: '2025-07-13T19:18:30',
      disciplines: [],
      results: []
    };

    component.openAddResultsDialog();
    tick();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(resultServiceSpy.createMultiple).toHaveBeenCalledWith(mockResults);
    expect(window.alert).toHaveBeenCalledWith('Resultados agregados correctamente');
    expect(loadSpy).toHaveBeenCalled();
  }));
});
