import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';

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
    FormsModule,
    NgForOf,
    RouterOutlet,
    RouterLink
  ],
  standalone: true
})
export class RankingComponent {
  filters = {
    nombre: '',
    apellido: '',
    disciplina: '',
    numeroLicencia: '',
    entrenador: '',
  };

  atletas: Atleta[] = [
    // Datos de ejemplo
    { nombre: 'Juan', apellido: 'Pérez', disciplina: '100m', numeroLicencia: '12345', entrenador: 'Carlos' },
    { nombre: 'Ana', apellido: 'Gómez', disciplina: 'Salto', numeroLicencia: '67890', entrenador: 'Luis' },
  ];

  paginatedAtletas: Atleta[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor() {
    this.updatePagination(); // Llamada inicial para llenar paginatedAtletas
  }
  /*
  private apiUrl = 'http://localhost:8080/api/athletes';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadAtletas();
  }

  loadAtletas(): void {
    const params: any = {
      page: this.currentPage - 1,
      size: this.itemsPerPage,
      ...this.filters
    };

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      response => {
        this.atletas = response.content;
        this.totalPages = response.totalPages;
        this.updatePagination();
      },
      error => {
        console.error('Error al cargar atletas:', error);
      }
    );
  }*/

  applyFilter(): void {
    this.currentPage = 1;
    //this.loadAtletas();
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      //this.loadAtletas();
      this.updatePagination();
    }
  }

  nextPage() {
    if (this.currentPage * this.itemsPerPage < this.atletas.length) {
      this.currentPage++;
      //this.loadAtletas();
      this.updatePagination();
    }
  }

  updatePagination() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    this.paginatedAtletas = this.atletas.slice(start, end);
  }

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }

}
