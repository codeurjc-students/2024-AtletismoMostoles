import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterOutlet} from '@angular/router';
import {NgForOf} from '@angular/common';

interface Coaches {
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
    RouterOutlet
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
  coaches: Coaches[] = [];
  paginatedCoaches: Coaches[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;

  constructor() {
    this.updatePagination(); // Llamada inicial para llenar paginatedAtletas
  }
  /*
  private apiUrl = 'http://localhost:8080/api/coaches';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadEntrenadores();
  }

  loadEntrenadores(): void {
    const params: any = {
      page: this.currentPage - 1,
      size: 10,
      ...this.filters
    };

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      response => {
        this.entrenadores = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al cargar entrenadores:', error);
      }
    );
  }*/

  applyFilters(): void {
    this.currentPage = 1;
    //this.loadEntrenadores();
  }

  updatePagination() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    this.paginatedCoaches = this.coaches.slice(start, end);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      //this.loadEntrenadores();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      //this.loadEntrenadores();
    }
  }

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
  ngOnInit(): void {
  }
}
