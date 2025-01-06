import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-association-atl',
  templateUrl: './association-atl.component.html',
  styleUrls: ['./association-atl.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf,
    RouterOutlet,
    RouterLink,
    HttpClientModule
  ]
})
export class AssociationAtlComponent implements OnInit {
  isModalOpen: boolean = false;
  disciplines: { id: number; name: string; image: string; coaches: string[] }[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalPages: number = 1;
  private apiUrl: string = 'http://localhost:8080/api/disciplines';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString())
      .set('sortBy', 'name');

    this.http.get<any>(this.apiUrl, { params }).subscribe(
      (response) => {
        this.disciplines = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar las disciplinas:', error);
      }
    );
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadDisciplines();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadDisciplines();
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

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  saveDiscipline(): void {
    if (this.newDiscipline.name && this.newDiscipline.description && this.newDiscipline.image) {
      this.http.post(this.apiUrl, this.newDiscipline).subscribe(
        () => {
          alert('Nueva disciplina agregada correctamente');
          this.closeModal();
          this.loadDisciplines();
        },
        (error) => {
          console.error('Error al agregar la disciplina:', error);
        }
      );
    } else {
      alert('Por favor, complete todos los campos antes de guardar.');
    }
  }

  newDiscipline: { name: string; description: string; image: string } = {
    name: '',
    description: '',
    image: ''
  };

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
