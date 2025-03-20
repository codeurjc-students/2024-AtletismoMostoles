import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { DatePipe, registerLocaleData } from '@angular/common';
import { LOCALE_ID } from '@angular/core';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.model';
import { Page } from '../../models/page.model';
import { AuthService } from '../../services/auth.service';
import { MatDatepickerModule, MatCalendar, MatCalendarCellClassFunction } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';
import localeEs from '@angular/common/locales/es';

registerLocaleData(localeEs);

@Component({
  selector: 'app-events-calendar',
  templateUrl: './events-calendar.component.html',
  styleUrls: ['./events-calendar.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    DatePipe,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatToolbarModule,
    MatMenuModule,
    MatListModule,
    RouterLink,
    RouterOutlet
  ],
  providers: [
    DatePipe,
    { provide: LOCALE_ID, useValue: 'es' }
  ]
})
export class EventsCalendarComponent implements OnInit, AfterViewInit {
  @ViewChild(MatCalendar) calendar!: MatCalendar<Date>;
  events: Event[] = [];
  filteredEvents: Event[] = [];
  selectedDate: Date = new Date();
  markedDates: Set<string> = new Set();
  isLoggedIn: boolean = false;

  constructor(
    private eventService: EventService,
    private router: Router,
    private authService: AuthService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadEvents();
  }

  ngAfterViewInit(): void {
    this.calendar.stateChanges.subscribe(() => {
      this.fetchEventsForMonth(this.calendar.activeDate);
    });
  }

  loadEvents(): void {
    this.fetchEventsForMonth(this.selectedDate);
  }

  fetchEventsForMonth(date: Date): void {
    const startDate = this.datePipe.transform(new Date(date.getFullYear(), date.getMonth(), 1), 'yyyy-MM-dd');
    const endDate = this.datePipe.transform(new Date(date.getFullYear(), date.getMonth() + 1, 0), 'yyyy-MM-dd');

    this.eventService.getAll(0, 100, 'date', startDate!, endDate!).subscribe(
      (response: Page<Event>) => {
        this.events = response.content;
        this.markEventsOnCalendar();
        this.filterEventsByDay();

        setTimeout(() => {
          console.log("🔄 Actualizando calendario...");
          this.calendar.updateTodaysDate();
        }, 100);
      },
      (error) => {
        console.error('Error al cargar eventos:', error);
      }
    );
  }

  markEventsOnCalendar(): void {
    this.markedDates.clear();
    this.events.forEach(event => {
      const eventDate = this.datePipe.transform(event.date, 'yyyy-MM-dd');
      if (eventDate) {
        this.markedDates.add(eventDate);
        console.log(`Evento marcado en el calendario: ${eventDate}`); // Para depuración
      }
    });
  }

  filterEventsByDay(): void {
    const selectedDay = this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd');
    this.filteredEvents = this.events.filter(event => {
      const eventDate = this.datePipe.transform(event.date, 'yyyy-MM-dd');
      return eventDate === selectedDay;
    });
  }

  dateChanged(event: Date | null): void {
    if (!event) {
      console.warn("⚠️ Se recibió un valor null en dateChanged");
      return;
    }
    this.selectedDate = event;
    this.filterEventsByDay();
  }

  dateClass: MatCalendarCellClassFunction<Date> = (date: Date) => {
    const dateString = this.datePipe.transform(date, 'yyyy-MM-dd');

    if (dateString) {
      console.log(`Revisión de fecha en el calendario: ${dateString}`); // 🔥 Debe aparecer en consola
    }

    if (dateString && this.markedDates.has(dateString)) {
      console.log(`✅ Día marcado en UI: ${dateString}`); // 🔥 Debe aparecer si la fecha tiene evento
      return 'event-highlight';
    }

    return '';
  };

  viewEvent(eventId: number): void {
    this.router.navigate([`/eventos/${eventId}`]);
  }

  navigateToNewEvent(): void {
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para crear un evento.');
      return;
    }
    this.router.navigate(['/events/new']);
  }

  goToToday(): void {
    this.selectedDate = new Date();
    this.fetchEventsForMonth(this.selectedDate);
    this.filterEventsByDay();
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }
}
