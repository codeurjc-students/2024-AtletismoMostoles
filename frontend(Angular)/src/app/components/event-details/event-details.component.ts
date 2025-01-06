import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';
import { NgForOf, NgIf, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Event {
  id: number;
  name: string;
  date: string;
  image: string;
  organizedByClub: boolean;
  address: string;
}

interface Result {
  nombre: string;
  apellido: string;
  disciplina: string;
  valor: string;
}

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
    image: '',
    organizedByClub: false,
    address: ''
  };

  results: Result[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  isEditing = false;
  mapUrl = '';
  private apiUrl = 'http://localhost:8080/api/events';

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const eventId = this.route.snapshot.params['id'];
    this.loadEvent(eventId);
    this.loadResults(eventId);
  }

  loadEvent(eventId: number): void {
    this.http.get<Event>(`${this.apiUrl}/${eventId}`).subscribe(
      (response) => {
        this.event = response;
        this.mapUrl = `https://www.google.com/maps/embed/v1/place?key=YOUR_API_KEY&q=${encodeURIComponent(response.address)}`;
      },
      (error) => {
        console.error('Error al cargar el evento:', error);
      }
    );
  }

  loadResults(eventId: number): void {
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString());

    this.http.get<any>(`${this.apiUrl}/${eventId}/results`, { params }).subscribe(
      (response) => {
        this.results = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar los resultados:', error);
      }
    );
  }

  toggleEdit(): void {
    this.isEditing = true;
  }

  saveEvent(): void {
    this.http.put(`${this.apiUrl}/${this.event.id}`, this.event).subscribe(
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
}
