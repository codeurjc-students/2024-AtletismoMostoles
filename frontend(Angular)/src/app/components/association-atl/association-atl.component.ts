import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {NgForOf} from '@angular/common';

@Component({
  selector: 'app-association-atl',
  templateUrl: './association-atl.component.html',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    RouterOutlet
  ],
  styleUrls: ['./association-atl.component.css']
})
export class AssociationAtlComponent implements OnInit {
  disciplines: { id: number; name: string; image: string; coaches: string[] }[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  private apiUrl: string = 'http://localhost:8080/api/disciplines'; // URL del backend

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.http.get<{ id: number; name: string; image: string; coaches: string[] }[]>(this.apiUrl).subscribe(
      (data) => {
        this.disciplines = data;
      },
      (error) => {
        console.error('Error al cargar las disciplinas:', error);
      }
    );
  }

  paginatedDisciplines(): { id: number; name: string; image: string; coaches: string[] }[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    return this.disciplines.slice(startIndex, endIndex);
  }

  totalPages(): number {
    return Math.ceil(this.disciplines.length / this.itemsPerPage);
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages()) {
      this.currentPage++;
    }
  }

  confirmDelete(disciplineId: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta disciplina?')) {
      this.deleteDiscipline(disciplineId);
    }
  }

  deleteDiscipline(disciplineId: number): void {
    this.http.delete(`${this.apiUrl}/${disciplineId}`).subscribe(
      () => {
        alert('Disciplina eliminada correctamente');
        this.loadDisciplines();
      },
      (error) => {
        console.error('Error al eliminar la disciplina:', error);
      }
    );
  }

  goToDetails(disciplineId: number): void {
    this.router.navigate(['/discipline-details', disciplineId]);
  }

  addNewDiscipline(): void {
    this.router.navigate(['/new-discipline']);
  }

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
