import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventsCalendarComponent } from './events-calendar.component';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { DatePipe } from '@angular/common';
import { MatCalendar } from '@angular/material/datepicker';
import { RouterTestingModule } from '@angular/router/testing';

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
      imports: [
        EventsCalendarComponent,
        HttpClientTestingModule,
        RouterTestingModule
      ],
      providers: [
        { provide: EventService, useValue: eventServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: {} },
        DatePipe
      ]
    }).compileComponents();

    TestBed.overrideComponent(EventsCalendarComponent, {
      set: { template: '' }
    });

    fixture = TestBed.createComponent(EventsCalendarComponent);
    component = fixture.componentInstance;

    // Stub the calendar so stateChanges emits once and activeDate is set
    component.calendar = {
      stateChanges: of(null),
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

    expect(component.fetchEventsForMonth).toHaveBeenCalledWith(
      component.calendar.activeDate
    );
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

  it('should return highlighted class for marked dates', () => {
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
