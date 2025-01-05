import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

interface Event {
  id: number;
  name: string;
  date: string;
  organizedByClub: boolean;
}

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
    RouterLink,
    NgClass,
    NgForOf,
    FormsModule,
    RouterOutlet,
    NgIf
  ],
  styleUrls: ['./events-calendar.component.css']
})
export class EventsCalendarComponent implements OnInit {
  events: Event[] = [];
  days: Day[] = [];
  currentMonth: string = '';
  currentYear: number = 0;
  selectedDay: number = 0;
  selectedDayName: string = '';
  inputDate: string = '';

  private apiUrl = 'http://localhost:8080/api/events';
  private today = new Date();
  private months = [
    'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio',
    'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
  ];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.currentMonth = this.months[this.today.getMonth()];
    this.currentYear = this.today.getFullYear();
    this.loadEvents();
  }

  loadEvents(): void {
    this.http.get<Event[]>(`${this.apiUrl}?month=${this.today.getMonth() + 1}&year=${this.today.getFullYear()}`).subscribe(
      response => {
        this.events = response;
        this.initCalendar();
      },
      error => {
        console.error('Error al cargar eventos:', error);
      }
    );
  }

  initCalendar(): void {
    const firstDay = new Date(this.currentYear, this.today.getMonth(), 1).getDay();
    const lastDate = new Date(this.currentYear, this.today.getMonth() + 1, 0).getDate();

    this.days = [];
    for (let i = 1; i <= lastDate; i++) {
      const hasEvent = this.events.some(event => new Date(event.date).getDate() === i);
      this.days.push({ date: i, prev: false, next: false, today: i === this.today.getDate(), hasEvent });
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

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}

