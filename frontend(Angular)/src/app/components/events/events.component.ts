import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';

interface Event {
  id: number;
  name: string;
  date: string;
  image: string;
  organizedByClub: boolean;
}

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    RouterOutlet,
    RouterLink,
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

  private apiUrl: string = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString())
      .set('sortBy', 'date')
      .set('organizer', this.selectedFilter);

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      (response) => {
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
}
