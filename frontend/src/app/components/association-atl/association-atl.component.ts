import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatMenuModule } from '@angular/material/menu';
import { NewDisciplineDialogComponent } from '../../modals/NewDisciplineDialogComponent.modal';

@Component({
  selector: 'app-association-atl',
  templateUrl: './association-atl.component.html',
  styleUrls: ['./association-atl.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    MatDialogModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatIconModule,
    MatGridListModule,
    MatToolbarModule,
    MatPaginatorModule,
    MatMenuModule,
    RouterLink,
    RouterOutlet,
  ],
})
export class AssociationAtlComponent implements OnInit {
  disciplines: any[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  isLoggedIn: boolean = false;
  disciplineForm: FormGroup;

  constructor(
    private disciplineService: DisciplineService,
    public dialog: MatDialog,
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.disciplineForm = this.fb.group({
      name: ['', Validators.required],
      schedule: ['', Validators.required],
      imageLink: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.disciplineService.getAll(this.currentPage - 1).subscribe(
      response => {
        console.log('Respuesta de la API:', response);
        if (response && response.content) {
          this.disciplines = response.content;
        } else if (Array.isArray(response)) {
          this.disciplines = response;
        } else {
          console.warn('Estructura inesperada en la respuesta de la API', response);
        }
        console.log('Disciplinas cargadas:', this.disciplines);
        this.totalPages = response.totalPages || 1;
      },
      error => {
        console.error('Error al cargar disciplinas:', error);
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

  goToDetails(id: number): void {
    this.router.navigate([`/discipline-details/${id}`]);
  }

  confirmDelete(id: number): void {
    if (confirm('¿Estás seguro de eliminar esta disciplina?')) {
      this.disciplineService.delete(id).subscribe(() => this.loadDisciplines());
    }
  }

  formatCoaches(coaches: any[]): string {
    return coaches.map(coach => coach.name).join(', ');
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(NewDisciplineDialogComponent, {
      width: '400px',
      data: { disciplineForm: this.disciplineForm }
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'save') {
        this.createDiscipline();
      }
    });
  }

  createDiscipline(): void {
    if (this.disciplineForm.valid) {
      this.disciplineService.create(this.disciplineForm.value).subscribe(
        () => {
          alert('Disciplina creada exitosamente');
          this.loadDisciplines();
        },
        (error) => {
          console.error('Error al crear disciplina:', error);
          alert('Error al crear disciplina');
        }
      );
    } else {
      alert('Por favor, complete todos los campos requeridos');
    }
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
  }
}
