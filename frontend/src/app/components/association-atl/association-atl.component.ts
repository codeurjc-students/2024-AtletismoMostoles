import { Component, OnInit } from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { DisciplineService } from '../../services/discipline.service';
import { Discipline } from '../../models/discipline.model';
import { Page } from '../../models/page.model';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-association-atl',
  templateUrl: './association-atl.component.html',
  styleUrls: ['./association-atl.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ]
})
export class AssociationAtlComponent implements OnInit {
  isModalOpen: boolean = false;
  disciplines: Discipline[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalPages: number = 1;
  isLoggedIn: boolean = false;

  newDiscipline: Discipline = { id: 0, name: '', description: '', imageLink: '', coaches: [] };

  constructor(
    private disciplineService: DisciplineService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.disciplineService.getAll(this.currentPage - 1, this.itemsPerPage).subscribe(
      (response: Page<Discipline>) => {
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
    if (!this.isLoggedIn) {
      alert('No tienes permiso para realizar esta acción.');
      return;
    }
    if (confirm('¿Estás seguro de que deseas eliminar esta disciplina?')) {
      this.deleteDiscipline(disciplineId);
    }
  }

  deleteDiscipline(disciplineId: number): void {
    this.disciplineService.delete(disciplineId).subscribe(
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
    if (!this.isLoggedIn) {
      alert('No tienes permiso para realizar esta acción.');
      return;
    }
    if (this.newDiscipline.name && this.newDiscipline.description && this.newDiscipline.imageLink) {
      this.disciplineService.create(this.newDiscipline).subscribe(
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
  formatCoaches(coaches: { firstName: string; lastName: string }[] | undefined): string {
    return coaches ? coaches.map(coach => `${coach.firstName} ${coach.lastName}`).join(', ') : 'Sin entrenadores';
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    if(!this.isLoggedIn){
      this.router.navigate(['/login']);
    }
    this.authService.logout();
  }

}
