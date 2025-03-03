import { Component, OnInit } from '@angular/core';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { Coach } from '../../models/coach.model';
import { Discipline } from '../../models/discipline.model';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule, NgIf, NgForOf } from '@angular/common';
import { NewCoachDialogComponent } from '../../models/NewCoachDialogComponent.model';
import { MatMenuModule } from '@angular/material/menu';
import { MatSortModule } from '@angular/material/sort';

@Component({
  selector: 'app-clubmembers',
  templateUrl: './clubmembers.component.html',
  styleUrls: ['./clubmembers.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    MatPaginatorModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatMenuModule,
    MatSortModule,
    RouterLink,
    RouterOutlet
  ],
})
export class ClubMembersComponent implements OnInit {
  filters = { firstName: '', lastName: '', licenseNumber: '', discipline: '' };
  dataSource = new MatTableDataSource<Coach>();
  displayedColumns: string[] = ['licenseNumber', 'firstName', 'lastName', 'disciplines'];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  disciplines: Discipline[] = [];
  coachForm: FormGroup;
  isAdmin: boolean = false;
  isLoggedIn: boolean = false;

  constructor(
    private coachService: CoachService,
    private disciplineService: DisciplineService,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.coachForm = this.fb.group({
      licenseNumber: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      disciplines: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe((user) => {
      this.isAdmin = this.authService.isAdmin();
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadCoaches();
    this.loadDisciplines();
  }

  loadCoaches(): void {
    this.coachService.getAll(this.currentPage - 1, this.itemsPerPage).subscribe(
      (response) => {
        this.dataSource.data = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar entrenadores:', error);
      }
    );
  }

  loadDisciplines(): void {
    this.disciplineService.getAll().subscribe(
      (response) => {
        this.disciplines = response.content;
      },
      (error) => {
        console.error('Error al cargar disciplinas:', error);
      }
    );
  }

  applyFilters(): void {
    this.currentPage = 1;
    this.coachService.getFiltered(this.filters, this.currentPage - 1, this.itemsPerPage).subscribe(
      (response) => {
        this.dataSource.data = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al filtrar entrenadores:', error);
      }
    );
  }
  openNewCoachDialog(): void {
    if (!this.isAdmin) {
      alert('Solo los administradores pueden crear entrenadores.');
      return;
    }
    const dialogRef = this.dialog.open(NewCoachDialogComponent, {
      width: '400px',
      data: { coachForm: this.coachForm, disciplines: this.disciplines }
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'save') {
        this.createCoach();
      }
    });
  }
  createCoach(): void {
    if (!this.isAdmin) {
      alert('Solo los administradores pueden crear entrenadores.');
      return;
    }
    if (this.coachForm.valid) {
      const formValue = this.coachForm.value;
      const disciplines = formValue.disciplines.map((id: number) => ({ id }));
      const newCoach: Coach = {
        ...formValue,
        disciplines: disciplines
      };
      this.coachService.create(newCoach).subscribe(
        () => {
          alert('Entrenador creado exitosamente');
          this.loadCoaches();
        },
        (error) => {
          console.error('Error al crear entrenador:', error);
          alert('Error al crear entrenador');
        }
      );
    } else {
      alert('Por favor, complete todos los campos requeridos');
    }
  }
  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadCoaches();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadCoaches();
    }
  }

  formatDisciplines(disciplines: Discipline[] | undefined): string {
    return disciplines ? disciplines.map(d => d.name).join(', ') : '';
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }

  goToCoachProfile(coach: Coach): void {
    if (coach && coach.licenseNumber) {
      this.router.navigate(['/profile', 'coach', coach.licenseNumber]);
    }
  }
}
