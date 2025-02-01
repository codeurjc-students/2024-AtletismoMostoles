import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import { NgForOf, NgIf, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { ResultService } from '../../services/result.service';
import { Event } from '../../models/event.model';
import { Results } from '../../models/results.model';
import { Page } from '../../models/page.model';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  standalone: true,
  imports: [
    NgStyle,
    FormsModule,
    NgIf,
    NgForOf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  event: Event = {
    id: 0,
    name: '',
    date: '',
    imageLink: '',
    isOrganizedByClub: false,
    disciplines: [],
    results: []
  };

  results: Results[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  isEditing = false;
  isLoggedIn = false;
  mapUrl = '';

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private resultService: ResultService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const eventId = this.route.snapshot.params['id'];
    this.isLoggedIn = this.authService.isAuthenticated();
    this.loadEvent(eventId);
    this.loadResults(eventId);
  }

  loadEvent(eventId: number): void {
    this.eventService.getById(eventId).subscribe(
      (response) => {
        this.event = response;
        this.mapUrl = `https://www.google.com/maps/embed/v1/place?key=YOUR_API_KEY&q=${encodeURIComponent(response.mapLink || '')}`;
      },
      (error) => {
        console.error('Error al cargar el evento:', error);
      }
    );
  }

  loadResults(eventId: number): void {
    this.resultService.getAll(this.currentPage - 1, this.itemsPerPage, 'date', eventId).subscribe(
      (response: Page<Results>) => {
        this.results = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar los resultados:', error);
      }
    );
  }

  toggleEdit(): void {
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para editar este evento.');
      return;
    }
    this.isEditing = !this.isEditing;
  }

  saveEvent(): void {
    if (!this.isLoggedIn) {
      alert('No tienes permiso para modificar este evento.');
      return;
    }
    this.eventService.update(this.event.id, this.event).subscribe(
      () => {
        this.isEditing = false;
        alert('Evento actualizado correctamente');
      },
      (error) => {
        console.error('Error al guardar el evento:', error);
        alert('Error al guardar el evento');
      }
    );
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.loadEvent(this.event.id);
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadResults(this.event.id);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadResults(this.event.id);
    }
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
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
}
