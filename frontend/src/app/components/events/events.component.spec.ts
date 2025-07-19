import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventsComponent } from './events.component';
import { Router, ActivatedRoute } from '@angular/router';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('EventsComponent', () => {
  let component: EventsComponent;
  let fixture: ComponentFixture<EventsComponent>;
  let eventServiceSpy: jasmine.SpyObj<EventService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const baseEvent = {
    mapLink: '',
    imageLink: '',
    creationTime: '2025-01-01T12:00:00',
    disciplines: [],
    results: []
  };

  beforeEach(async () => {
    eventServiceSpy = jasmine.createSpyObj('EventService', ['getAll', 'delete']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [EventsComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: {} }
      ]
    }).compileComponents();

    TestBed.overrideComponent(EventsComponent, {
      set: { template: '' }
    });

    fixture = TestBed.createComponent(EventsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load events', () => {
    const mockResponse = {
      content: [
        { id: 1, name: 'Evento A', date: '2025-01-01', organizedByClub: true, ...baseEvent }
      ],
      totalElements: 1,
      totalPages: 1,
      size: 6,
      number: 0
    };
    eventServiceSpy.getAll.and.returnValue(of(mockResponse));

    component.loadEvents();

    expect(eventServiceSpy.getAll).toHaveBeenCalledWith(0, 6, 'date');
    expect(component.allEvents.length).toBe(1);
    expect(component.totalPages).toBe(1);
  });

  it('should filter events by club', () => {
    component.allEvents = [
      { id: 1, name: 'Evento Club', date: '2025-01-01', organizedByClub: true, ...baseEvent },
      { id: 2, name: 'Evento Externo', date: '2025-01-02', organizedByClub: false, ...baseEvent }
    ];
    component.selectedFilter = 'club';
    component.applyFilter();

    expect(component.events.length).toBe(1);
    expect(component.events[0].organizedByClub).toBeTrue();
  });

  it('should filter events by external', () => {
    component.allEvents = [
      { id: 1, name: 'Evento Club', date: '2025-01-01', organizedByClub: true, ...baseEvent },
      { id: 2, name: 'Evento Externo', date: '2025-01-02', organizedByClub: false, ...baseEvent }
    ];
    component.selectedFilter = 'external';
    component.applyFilter();

    expect(component.events.length).toBe(1);
    expect(component.events[0].organizedByClub).toBeFalse();
  });

  it('should filter upcoming events', () => {
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 5);
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 5);

    component.allEvents = [
      { id: 1, name: 'Evento Futuro', date: futureDate.toISOString(), organizedByClub: true, ...baseEvent },
      { id: 2, name: 'Evento Pasado', date: pastDate.toISOString(), organizedByClub: true, ...baseEvent }
    ];
    component.selectedFilter = 'upcoming';
    component.applyFilter();

    expect(component.events.length).toBe(1);
    expect(new Date(component.events[0].date) > new Date()).toBeTrue();
  });

  it('should reset filters when selecting all', () => {
    component.allEvents = [
      { id: 1, name: 'Evento A', date: '2025-01-01', organizedByClub: true, ...baseEvent }
    ];
    component.selectedFilter = 'all';
    component.applyFilter();

    expect(component.events.length).toBe(1);
  });

  it('should navigate to event details', () => {
    component.viewEvent(1);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/eventos/1']);
  });

  it('should go to next page', () => {
    component.currentPage = 1;
    component.totalPages = 2;
    eventServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 6,
      number: 2
    }));

    component.nextPage();
    expect(component.currentPage).toBe(2);
    expect(eventServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should go to previous page', () => {
    component.currentPage = 2;
    eventServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 2,
      size: 6,
      number: 1
    }));

    component.prevPage();
    expect(component.currentPage).toBe(1);
    expect(eventServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should navigate to new event creation if logged in', () => {
    component.isLoggedIn = true;
    component.navigateToNewEvent();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/events/new']);
  });

  it('should alert if not logged in when trying to add new event', () => {
    component.isLoggedIn = false;
    spyOn(window, 'alert').and.stub();
    component.navigateToNewEvent();
    expect(window.alert).toHaveBeenCalledWith('Debes iniciar sesión para crear un evento.');
  });

  it('should navigate to login', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call logout', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should delete an event and reload', () => {
    eventServiceSpy.delete.and.returnValue(of(void 0));
    eventServiceSpy.getAll.and.returnValue(of({
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 6,
      number: 0
    }));
    spyOn(window, 'alert').and.stub();

    component.deleteEvent(1);

    expect(eventServiceSpy.delete).toHaveBeenCalledWith(1);
    expect(window.alert).toHaveBeenCalledWith('Evento eliminado correctamente');
    expect(eventServiceSpy.getAll).toHaveBeenCalled();
  });
});
