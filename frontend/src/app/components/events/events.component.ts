import { Component, OnInit } from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { EventService } from '../../services/event.service';
import { Event } from '../../models/event.model';
import { Page } from '../../models/page.model';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: Event[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 6;
  totalPages: number = 1;
  selectedFilter: string = 'upcoming';
  isLoggedIn: boolean = false;

  constructor(
    private eventService: EventService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });    console.log("esta logeado:", this.isLoggedIn);
    this.loadEvents();
  }

  loadEvents(): void {
    this.eventService.getAll(this.currentPage - 1, this.itemsPerPage, 'date').subscribe(
      (response: Page<Event>) => {
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

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
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
    if(!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    this.authService.logout();
  }

  deleteEvent(eventId: number) {
    this.eventService.delete(eventId).subscribe(
      () => {
        alert('Evento eliminada correctamente');
        this.loadEvents();
      },
      (error) => {
        console.error('Error al eliminar el evento:', error);
      }
    );
  }
}
