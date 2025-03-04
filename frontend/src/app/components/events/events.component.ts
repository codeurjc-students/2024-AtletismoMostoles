import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.model';
import { Page } from '../../models/page.model';
import { HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    HttpClientModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatGridListModule,
    MatToolbarModule,
    MatPaginatorModule,
    MatMenuModule,
    RouterLink,
    RouterOutlet,

  ],
})
export class EventsComponent implements OnInit {
  events: Event[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalPages: number = 1;
  selectedFilter: string = 'all';
  isLoggedIn: boolean = false;

  constructor(
    private eventService: EventService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventService.getAll(this.currentPage - 1, this.itemsPerPage, 'date').subscribe(
      (response: Page<Event>) => {
        console.log('Eventos cargados:', response);
        this.events = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar eventos:', error);
      }
    );
  }

  applyFilter(): void {
    this.currentPage = 1;
    this.loadEvents();
  }

  viewEvent(eventId: number): void {
    this.router.navigate([`/eventos/${eventId}`]);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadEvents();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadEvents();
    }
  }

  navigateToNewEvent(): void {
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para crear un evento.');
      return;
    }
    this.router.navigate(['/events/new']);
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
  }

  deleteEvent(eventId: number) {
    this.eventService.delete(eventId).subscribe(
      () => {
        alert('Evento eliminado correctamente');
        this.loadEvents();
      },
      (error) => {
        console.error('Error al eliminar el evento:', error);
      }
    );
  }
}
