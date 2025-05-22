import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Results } from '../../models/results.model';
import { Observable } from 'rxjs';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Discipline } from '../../models/discipline.model';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    RouterLink,
    RouterOutlet,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatDialogModule,
    MatMenuModule,
    MatListModule
  ],
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile!: Athlete | Coach;
  coachedAthletes: Athlete[] = [];
  results: Results[] = [];
  paginatedResults: Results[] = [];
  disciplines: Discipline[] = [];
  availableDisciplines: Discipline[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  profileForm!: FormGroup;
  isEditing: boolean = false;
  isAthlete = true;
  errorMessage: string = '';
  isAdmin: boolean = false;
  isLoggedIn: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private athleteService: AthleteService,
    private coachService: CoachService,
    private fb: FormBuilder,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const type = this.route.snapshot.params['type'];
    const id = this.route.snapshot.params['id'];
    this.isAthlete = type === 'athlete';
    this.authService.user.subscribe(() => {
      this.isAdmin = this.authService.isAdmin();
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadProfile(id);
    this.loadAvailableDisciplines();
  }

  loadProfile(id: number): void {
    const service = this.isAthlete ? this.athleteService : this.coachService;
    (service.getById(id.toString()) as Observable<Athlete | Coach>).subscribe(
      (response) => {
        this.profile = response;
        this.disciplines = Array.isArray(response.disciplines) ? response.disciplines : [];

        if (this.isAthlete) {
          const athlete = response as Athlete;
          this.results = athlete.results || [];
          this.totalPages = Math.ceil(this.results.length / this.itemsPerPage);
          this.updatePagination();
        } else {
          const coach = response as Coach;
          this.coachedAthletes = coach.athletes || [];
        }
        console.log('Resultados:', this.results);
        console.log('Ejemplo evento:', this.results[0]?.event);
      },
      (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Error al cargar el perfil. Inténtalo de nuevo más tarde.';
      }
    );
  }

  loadAvailableDisciplines(): void {
    this.athleteService.getAll().subscribe(
      (response) => {
        this.availableDisciplines = Array.isArray(response) ? response : [];
      },
      (error) => {
        console.error('Error cargando disciplinas:', error);
      }
    );
  }

  enableEdit(): void {
    if (!this.isAdmin) return;
    this.isEditing = true;
    this.profileForm = this.fb.group({
      firstName: [this.profile.firstName, Validators.required],
      lastName: [this.profile.lastName, Validators.required],
      licenseNumber: [this.profile.licenseNumber, Validators.required],
      birthDate: [this.isAthlete ? (this.profile as Athlete).birthDate : ''],
      disciplines: [this.profile.disciplines?.map(d => d.id)]
    });
  }

  saveProfile(): void {
    if (!this.isAdmin || !this.profileForm.valid) return;
    const updatedProfile = {
      ...this.profile,
      ...this.profileForm.value,
      disciplines: this.profileForm.value.disciplines.map((id: number) => ({ id }))
    };
    this.athleteService.update(updatedProfile.licenseNumber, updatedProfile).subscribe(
      () => {
        alert('Perfil actualizado con éxito');
        this.isEditing = false;
        this.loadProfile(updatedProfile.licenseNumber);
      },
      (error) => {
        console.error('Error actualizando perfil:', error);
        alert('Error actualizando perfil.');
      }
    );
  }

  deleteProfile(): void {
    if (!this.isAdmin || !confirm('¿Seguro que quieres eliminar este perfil?')) return;
    const service = this.isAthlete ? this.athleteService : this.coachService;
    service.delete(this.profile.licenseNumber).subscribe(() => {
      this.router.navigate([this.isAthlete ? '/ranking' : '/miembros']);
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    this.paginatedResults = this.results.slice(start, end);
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination();
    }
  }

  get formattedBirthDate(): string {
    if (!this.isAthlete || !(this.profile as Athlete)?.birthDate) return 'No disponible';
    return (this.profile as Athlete).birthDate;
  }

  get firstName(): string {
    return this.profile?.firstName || '';
  }

  get lastName(): string {
    return this.profile?.lastName || '';
  }

  get licenseNumber(): string {
    return this.profile?.licenseNumber || '';
  }

  get profileDisciplines(): Discipline[] {
    return this.profile?.disciplines || [];
  }

  get coachName(): string {
    if (
      !this.isAthlete ||
      !this.profile ||
      !('coach' in this.profile) ||
      !this.profile.coach
    ) {
      return 'No disponible';
    }

    return `${this.profile.coach.firstName} ${this.profile.coach.lastName}`;
  }


}
