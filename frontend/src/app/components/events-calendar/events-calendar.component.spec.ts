import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventsCalendarComponent } from './events-calendar.component';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DatePipe } from '@angular/common';
import { MatCalendar } from '@angular/material/datepicker';

describe('EventsCalendarComponent', () => {
  let component: EventsCalendarComponent;
  let fixture: ComponentFixture<EventsCalendarComponent>;
  let eventServiceSpy: jasmine.SpyObj<EventService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    eventServiceSpy = jasmine.createSpyObj('EventService', ['getAll']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated'], { user: of(null) });
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ EventsCalendarComponent, HttpClientTestingModule ],
      providers: [
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        DatePipe
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventsCalendarComponent);
    component = fixture.componentInstance;

    // Mock del calendario para ngAfterViewInit
    component.calendar = {
      stateChanges: of(),
      updateTodaysDate: jasmine.createSpy('updateTodaysDate'),
      activeDate: new Date()
    } as unknown as MatCalendar<Date>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize and load events', () => {
    const mockPage = {
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 100,
      number: 0
    };
    eventServiceSpy.getAll.and.returnValue(of(mockPage));

    component.ngOnInit();

    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(eventServiceSpy.getAll).toHaveBeenCalled();
  });

  it('should subscribe to stateChanges in AfterViewInit', () => {
    spyOn(component, 'fetchEventsForMonth');
    component.ngAfterViewInit();
    expect(component.fetchEventsForMonth).toHaveBeenCalledWith(component.calendar.activeDate);
  });

  it('should fetch events and mark dates', () => {
    const mockPage = {
      content: [
        { id: 1, name: 'Evento A', date: '2025-10-10', organizedByClub: true }
      ],
      totalElements: 1,
      totalPages: 1,
      size: 100,
      number: 0
    };
    eventServiceSpy.getAll.and.returnValue(of(mockPage));

    component.fetchEventsForMonth(new Date('2025-10-01'));

    expect(eventServiceSpy.getAll).toHaveBeenCalled();
    expect(component.events.length).toBe(1);
    expect(component.markedDates.size).toBe(1);
  });

  it('should update filteredEvents on dateChanged', () => {
    const testDate = new Date('2025-10-10');
    component.events = [
      { id: 1, name: 'Evento A', date: '2025-10-10', organizedByClub: true }
    ];
    component.dateChanged(testDate);
    expect(component.filteredEvents.length).toBe(1);
    expect(component.filteredEvents[0].id).toBe(1);
  });

  it('should handle null value in dateChanged gracefully', () => {
    spyOn(console, 'warn');
    component.dateChanged(null);
    expect(console.warn).toHaveBeenCalledWith('⚠️ Se recibió un valor null en dateChanged');
  });

  it('should navigate to event details', () => {
    component.viewEvent(1);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/eventos/1']);
  });

  it('should navigate to today', () => {
    spyOn(component, 'fetchEventsForMonth');
    spyOn(component, 'filterEventsByDay');

    component.goToToday();

    expect(component.fetchEventsForMonth).toHaveBeenCalled();
    expect(component.filterEventsByDay).toHaveBeenCalled();
  });

  it('should navigate to login', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should call logout', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('should navigate to new event creation if logged in', () => {
    component.isLoggedIn = true;
    component.navigateToNewEvent();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/events/new']);
  });

  it('should alert if not logged in when trying to add new event', () => {
    component.isLoggedIn = false;
    spyOn(window, 'alert');
    component.navigateToNewEvent();
    expect(window.alert).toHaveBeenCalledWith('Debes iniciar sesión para crear un evento.');
  });

  it('should return class name for marked dates', () => {
    const testDate = new Date('2025-10-10');
    const datePipe = TestBed.inject(DatePipe);
    const dateString = datePipe.transform(testDate, 'yyyy-MM-dd')!;
    component.markedDates.add(dateString);

    const result = component.dateClass(testDate, 'month');
    expect(result).toBe('event-highlight');
  });

  it('should return empty string for unmarked dates', () => {
    const testDate = new Date('2025-10-11');
    const result = component.dateClass(testDate, 'month');
    expect(result).toBe('');
  });
});
