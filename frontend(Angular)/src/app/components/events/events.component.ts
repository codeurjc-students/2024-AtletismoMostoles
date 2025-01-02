import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

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
    RouterLink,
    RouterOutlet
  ],
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {
  events: Event[] = [];
  paginatedEvents: Event[] = [];
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  selectedFilter = 'upcoming';

  private apiUrl = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    const params: any = {
      page: this.currentPage - 1,
      size: this.itemsPerPage,
      filter: this.selectedFilter
    };

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      response => {
        this.events = response.content;
        this.totalPages = response.totalPages;
        this.updatePagination();
      },
      error => {
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

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    this.paginatedEvents = this.events.slice(start, end);
  }

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
