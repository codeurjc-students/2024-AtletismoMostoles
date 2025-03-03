import { Component, OnInit } from '@angular/core';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Discipline } from '../../models/discipline.model';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatMenuModule } from '@angular/material/menu';
import { CommonModule, NgIf, NgForOf } from '@angular/common';
import { NewAthleteDialogComponent } from '../../models/NewAthleteDialogComponent.model';

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  styleUrls: ['./ranking.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatToolbarModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSortModule,
    MatCheckboxModule,
    MatMenuModule,
    RouterOutlet,
    RouterLink
  ],
})
export class RankingComponent implements OnInit {
  filters = { firstName: '', lastName: '', discipline: '', licenseNumber: '', coach: '' };
  dataSource = new MatTableDataSource<Athlete>();
  displayedColumns: string[] = ['licenseNumber', 'firstName', 'lastName', 'disciplines', 'coach'];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  coaches: Coach[] = [];
  disciplines: Discipline[] = [];
  athleteForm: FormGroup;
  isAdmin: boolean = false;
  isLoggedIn: boolean = false;

  constructor(
    private athleteService: AthleteService,
    private coachService: CoachService,
    private disciplineService: DisciplineService,
    private dialog: MatDialog,
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.athleteForm = this.fb.group({
      licenseNumber: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: ['', Validators.required],
      coach: ['', Validators.required],
      disciplines: [[], Validators.required],
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe((user) => {
      this.isAdmin = this.authService.isAdmin();
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadAthletes();
    this.loadCoaches();
    this.loadDisciplines();
  }

  loadCoaches(): void {
    this.coachService.getAll().subscribe(
      (response) => {
        this.coaches = response.content;
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

  loadAthletes(): void {
    this.athleteService.getAll(this.currentPage - 1, this.itemsPerPage).subscribe(
      (response) => {
        this.dataSource.data = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al cargar atletas:', error);
      }
    );
  }

  formatDisciplines(disciplines: Discipline[] | undefined): string {
    return disciplines ? disciplines.map(d => d.name).join(', ') : '';
  }

  openNewAthleteDialog(): void {
    if (!this.isAdmin) {
      alert('Solo los administradores pueden crear atletas.');
      return;
    }
    const dialogRef = this.dialog.open(NewAthleteDialogComponent, {
      width: '400px',
      data: {
        athleteForm: this.athleteForm,
        coaches: this.coaches,
        disciplines: this.disciplines
      }
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result === 'save') {
        this.createAthlete();
      }
    });
  }

  createAthlete(): void {
    if (!this.isAdmin) {
      alert('Solo los administradores pueden crear atletas.');
      return;
    }
    if (this.athleteForm.valid) {
      const formValue = this.athleteForm.value;
      const disciplines = formValue.disciplines.map((id: number) => ({ id }));
      const newAthlete: Athlete = {
        ...formValue,
        coach: { licenseNumber: formValue.coach },
        disciplines: disciplines,
      };
      this.athleteService.create(newAthlete).subscribe(
        () => {
          alert('Atleta creado exitosamente');
          this.loadAthletes();
        },
        (error) => {
          console.error('Error al crear atleta:', error);
          alert('Error al crear atleta');
        }
      );
    } else {
      alert('Por favor, complete todos los campos requeridos');
    }
  }

  applyFilter(): void {
    this.currentPage = 1;
    this.athleteService.getFiltered(this.filters, this.currentPage - 1, this.itemsPerPage).subscribe(
      (response) => {
        this.dataSource.data = response.content;
        this.totalPages = response.totalPages;
      },
      (error) => {
        console.error('Error al filtrar atletas:', error);
      }
    );
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadAthletes();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadAthletes();
    }
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }


  goToAthleteProfile(licenseNumber: String) {
    if (licenseNumber) {
      this.router.navigate(['/profile', 'athlete', licenseNumber]);
    }
  }
}
