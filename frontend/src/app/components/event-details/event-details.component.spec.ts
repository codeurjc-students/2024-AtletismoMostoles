import { ComponentFixture, TestBed } from '@angular/core/testing';
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

describe('EventDetailsComponent', () => {
  let component: EventDetailsComponent;
  let fixture: ComponentFixture<EventDetailsComponent>;
  let eventServiceSpy: jasmine.SpyObj<EventService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let athleteServiceSpy: jasmine.SpyObj<AthleteService>;
  let resultServiceSpy: jasmine.SpyObj<ResultService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let sanitizerSpy: jasmine.SpyObj<DomSanitizer>;

  beforeEach(async () => {
    const routeStub = {
      snapshot: { paramMap: { get: () => '1' } }
    };

    eventServiceSpy = jasmine.createSpyObj('EventService', ['getById', 'update']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    athleteServiceSpy = jasmine.createSpyObj('AthleteService', ['getAll']);
    resultServiceSpy = jasmine.createSpyObj('ResultService', ['createMultiple']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    sanitizerSpy = jasmine.createSpyObj('DomSanitizer', ['bypassSecurityTrustResourceUrl']);

    await TestBed.configureTestingModule({
      imports: [EventDetailsComponent, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: AthleteService, useValue: athleteServiceSpy },
        { provide: ResultService, useValue: resultServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: DomSanitizer, useValue: sanitizerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventDetailsComponent);
    component = fixture.componentInstance;
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
      results: [],
      disciplines: []
    };
    eventServiceSpy.getById.and.returnValue(of(mockEvent));
    sanitizerSpy.bypassSecurityTrustResourceUrl.and.returnValue('sanitized-url' as any);

    component.loadEvent();

    expect(eventServiceSpy.getById).toHaveBeenCalledWith(1);
    expect(component.event).toEqual(mockEvent);
    expect(component.eventForm.value.name).toBe('Maratón');
    expect(component.sanitizedMapUrl).toBe('sanitized-url' as any);
  });

  it('should toggle edit mode', () => {
    expect(component.isEditing).toBeFalse();
    component.toggleEdit();
    expect(component.isEditing).toBeTrue();
  });

  it('should save event if form is valid', () => {
    component.event = {
      id: 1,
      name: 'Evento',
      date: '2025-10-10',
      organizedByClub: true,
      imageLink: 'link.jpg',
      mapLink: 'maplink'
    };
    component.eventForm.setValue({
      name: 'Nuevo nombre',
      date: '2025-11-11',
      organizedByClub: true,
      imageLink: 'nuevo-link.jpg'
    });
    eventServiceSpy.update.and.returnValue(of({
      id: 1,
      name: 'Nuevo nombre',
      date: '2025-11-11',
      organizedByClub: true,
      imageLink: 'nuevo-link.jpg',
      mapLink: 'maplink'
    }));
    spyOn(window, 'alert');
    spyOn(component, 'loadEvent');

    component.saveEvent();

    expect(eventServiceSpy.update).toHaveBeenCalledWith(1, {
      id: 1,
      name: 'Nuevo nombre',
      date: '2025-11-11',
      organizedByClub: true,
      imageLink: 'nuevo-link.jpg',
      mapLink: 'maplink'
    });
    expect(window.alert).toHaveBeenCalledWith('Evento actualizado con éxito');
    expect(component.loadEvent).toHaveBeenCalled();
    expect(component.isEditing).toBeFalse();
  });

  it('should not update if form is invalid', () => {
    component.eventForm.setValue({
      name: '',
      date: '',
      organizedByClub: false,
      imageLink: ''
    });
    component.saveEvent();
    expect(eventServiceSpy.update).not.toHaveBeenCalled();
  });

  it('should decrease page on prevPage()', () => {
    component.currentPage = 2;
    component.prevPage();
    expect(component.currentPage).toBe(1);
  });

  it('should not go below page 1', () => {
    component.currentPage = 1;
    component.prevPage();
    expect(component.currentPage).toBe(1);
  });

  it('should increase page on nextPage()', () => {
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

  it('should navigate to login', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call authService.logout()', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should open add results dialog and save results', () => {
    const mockAthletes = {
      content: [{
        licenseNumber: 'A001',
        firstName: 'John',
        lastName: 'Doe',
        birthDate: '2000-01-01'
      }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };

    athleteServiceSpy.getAll.and.returnValue(of(mockAthletes));

    const afterClosedSpy = jasmine.createSpyObj({ subscribe: (fn: any) => fn([{ value: 10 }]) });
    dialogSpy.open.and.returnValue({ afterClosed: () => afterClosedSpy } as any);
    resultServiceSpy.createMultiple.and.returnValue(of([])); // devuelve un array vacío
    spyOn(window, 'alert');
    spyOn(component, 'loadEvent');

    component.event = { id: 1, disciplines: [] };
    component.openAddResultsDialog();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(resultServiceSpy.createMultiple).toHaveBeenCalledWith([{ value: 10 }]);
    expect(window.alert).toHaveBeenCalledWith('Resultados agregados correctamente');
    expect(component.loadEvent).toHaveBeenCalled();
  });
});
