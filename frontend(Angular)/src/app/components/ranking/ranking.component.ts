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

  constructor() {
    this.updatePagination(); // Llamada inicial para llenar paginatedAtletas
  }

  applyFilter() {
    // Lógica para aplicar filtros
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  nextPage() {
    if (this.currentPage * this.itemsPerPage < this.atletas.length) {
      this.currentPage++;
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
