import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink, RouterOutlet } from '@angular/router';
import { NgForOf } from '@angular/common';

interface Coach {
  nombre: string;
  apellido: string;
  numeroLicencia: string;
  disciplina: string;
}

@Component({
  selector: 'app-clubmembers',
  templateUrl: './clubmembers.component.html',
  standalone: true,
  imports: [
    FormsModule,
    RouterLink,
    NgForOf,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./clubmembers.component.css']
})
export class ClubMembersComponent implements OnInit {
  filters = {
    nombre: '',
    apellido: '',
    numeroLicencia: '',
    disciplina: ''
  };
  coaches: Coach[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  private apiUrl = 'http://localhost:8080/api/coaches';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadCoaches();
  }

  loadCoaches(): void {
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString());

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      response => {
        this.coaches = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al cargar entrenadores:', error);
      }
    );
    console.log(this.coaches);
  }

  applyFilters(): void {
    this.currentPage = 1;
    this.loadCoaches();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadCoaches();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadCoaches();
    }
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
