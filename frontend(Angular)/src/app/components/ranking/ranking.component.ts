import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgForOf } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';

export interface Atleta {
  nombre: string;
  apellido: string;
  disciplina: string;
  numeroLicencia: string;
  entrenador: string;
}

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.css'],
  imports: [
    RouterLink,
    FormsModule,
    NgForOf,
    RouterOutlet,
    HttpClientModule
  ],
  standalone: true
})
export class RankingComponent implements OnInit {
  filters = {
    nombre: '',
    apellido: '',
    disciplina: '',
    numeroLicencia: '',
    entrenador: '',
  };

  paginatedAtletas: Atleta[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  private apiUrl = 'http://localhost:8080/api/athletes';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadAtletas();
  }

  loadAtletas(): void {
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString())
      .set('sortBy', 'lastName'); // Sort by lastName by default

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      response => {
        this.paginatedAtletas = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al cargar atletas:', error);
      }
    );
  }

  applyFilter(): void {
    this.currentPage = 1;
    this.loadAtletas();
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadAtletas();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadAtletas();
    }
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
