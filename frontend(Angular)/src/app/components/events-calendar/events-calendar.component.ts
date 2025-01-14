import { Component, OnInit } from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { NgClass, NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.model';
import { Page } from '../../models/page.model';
import {HttpClientModule} from '@angular/common/http';

interface Day {
  date: number;
  prev: boolean;
  next: boolean;
  today: boolean;
  hasEvent: boolean;
}

@Component({
  selector: 'app-events-calendar',
  templateUrl: './events-calendar.component.html',
  standalone: true,
  imports: [
    NgClass,
    NgForOf,
    FormsModule,
    NgIf,
    RouterOutlet,
    RouterLink,
    HttpClientModule
  ],
  styleUrls: ['./events-calendar.component.css']
})
export class EventsCalendarComponent implements OnInit {
  events: Event[] = [];
  filteredEvents: Event[] = [];
  days: Day[] = [];
  currentMonth: string = '';
  currentYear: number = 0;
  selectedDay: number = 0;
  selectedDayName: string = '';
  inputDate: string = '';

  private today = new Date();
  private months = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio',
    'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
  ];

  constructor(
    private eventService: EventService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentMonth = this.months[this.today.getMonth()];
    this.currentYear = this.today.getFullYear();
    this.loadEvents();
  }

  loadEvents(): void {
    const startDate = new Date(this.currentYear, this.today.getMonth(), 1).toISOString().split('T')[0];
    const endDate = new Date(this.currentYear, this.today.getMonth() + 1, 0).toISOString().split('T')[0];

    this.eventService.getAll(0, 100, 'date', startDate, endDate).subscribe(
      (response: Page<Event>) => {
        this.events = response.content;
        this.initCalendar();
        this.filterEventsByDay();
      },
      (error) => {
        console.error('Error al cargar eventos:', error);
      }
    );
  }

  initCalendar(): void {
    const firstDay = new Date(this.currentYear, this.today.getMonth(), 1).getDay();
    const lastDate = new Date(this.currentYear, this.today.getMonth() + 1, 0).getDate();

    this.days = [];

    for (let i = 0; i < firstDay; i++) {
      this.days.push({ date: 0, prev: false, next: false, today: false, hasEvent: false });
    }

    for (let i = 1; i <= lastDate; i++) {
      const hasEvent = Array.isArray(this.events) && this.events.some((event) => new Date(event.date).getDate() === i);
      this.days.push({ date: i, prev: false, next: false, today: i === this.today.getDate(), hasEvent });
    }
  }

  filterEventsByDay(): void {
    if (this.selectedDay) {
      this.filteredEvents = this.events.filter(event => {
        const eventDate = new Date(event.date);
        return eventDate.getDate() === this.selectedDay &&
          eventDate.getMonth() === this.today.getMonth() &&
          eventDate.getFullYear() === this.currentYear;
      });
    } else {
      this.filteredEvents = [];
    }
  }

  prevMonth(): void {
    this.today.setMonth(this.today.getMonth() - 1);
    this.currentMonth = this.months[this.today.getMonth()];
    this.currentYear = this.today.getFullYear();
    this.loadEvents();
  }

  nextMonth(): void {
    this.today.setMonth(this.today.getMonth() + 1);
    this.currentMonth = this.months[this.today.getMonth()];
    this.currentYear = this.today.getFullYear();
    this.loadEvents();
  }

  selectDay(day: Day): void {
    this.selectedDay = day.date;
    this.selectedDayName = new Date(this.currentYear, this.today.getMonth(), day.date).toLocaleDateString('es-ES', { weekday: 'long' });
    this.filterEventsByDay();
  }

  gotoDate(): void {
    const [month, year] = this.inputDate.split('/').map(Number);
    if (month && year) {
      this.today.setMonth(month - 1);
      this.today.setFullYear(year);
      this.currentMonth = this.months[this.today.getMonth()];
      this.currentYear = this.today.getFullYear();
      this.loadEvents();
    } else {
      alert('Fecha no válida.');
    }
  }

  goToToday(): void {
    this.today = new Date();
    this.currentMonth = this.months[this.today.getMonth()];
    this.currentYear = this.today.getFullYear();
    this.loadEvents();
  }

  viewEvent(eventId: number): void {
    this.router.navigate([`/eventos/${eventId}`]);
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
