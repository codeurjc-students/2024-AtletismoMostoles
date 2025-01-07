import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';

interface Profile {
  id: number;
  numeroLicencia: string;
  nombre: string;
  apellido: string;
  entrenador?: string;
  entrenadorId?: number;
  fechaNacimiento: string;
  disciplinas?: string[];
  disciplina?: string;
}

interface Result {
  evento: string;
  disciplina: string;
  valor: string;
}

interface Athlete {
  firstName: string;
  lastName: string;
  fechaNacimiento: string;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    NgForOf,
    RouterOutlet
  ],
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: Profile = {
    id: 0,
    numeroLicencia: '',
    nombre: '',
    apellido: '',
    fechaNacimiento: ''
  };
  results: Result[] = [];
  paginatedResults: Result[] = [];
  paginatedAthletes: Athlete[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  isAthlete = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private athleteService: AthleteService,
    private coachService: CoachService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    const type = this.route.snapshot.queryParams['type'];
    this.isAthlete = type === 'athlete';
    this.loadProfile(id);
    if (this.isAthlete) {
      this.loadResults(id);
    } else {
      this.loadAthletes(id);
    }
  }

  loadProfile(id: number): void {
    const service = this.isAthlete ? this.athleteService : this.coachService;
    service.getById(id.toString()).subscribe(
      (response) => {
        this.profile = response;
      },
      (error) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Error loading profile. Please try again later.';
      }
    );
  }

  loadResults(id: number): void {
    this.athleteService.getById(id.toString()).subscribe(
      (response) => {
        this.results = response.results || [];
        this.totalPages = Math.ceil(this.results.length / this.itemsPerPage);
        this.updatePagination('results');
      },
      (error) => {
        console.error('Error loading results:', error);
        this.errorMessage = 'Error loading results. Please try again later.';
      }
    );
  }

  loadAthletes(id: number): void {
    this.coachService.getById(id.toString()).subscribe(
      (response) => {
        this.paginatedAthletes = response.athletes || [];
        this.totalPages = Math.ceil(this.paginatedAthletes.length / this.itemsPerPage);
        this.updatePagination('athletes');
      },
      (error) => {
        console.error('Error loading athletes:', error);
        this.errorMessage = 'Error loading athletes. Please try again later.';
      }
    );
  }

  updatePagination(type: 'results' | 'athletes'): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    if (type === 'results') {
      this.paginatedResults = this.results.slice(start, end);
    } else {
      this.paginatedAthletes = this.paginatedAthletes.slice(start, end);
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePagination(this.isAthlete ? 'results' : 'athletes');
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePagination(this.isAthlete ? 'results' : 'athletes');
    }
  }

  goBack(): void {
    this.router.navigate(['/eventos']);
  }

  editProfile(): void {
    alert('Edit functionality not implemented yet.');
  }

  deleteProfile(): void {
    if (confirm('Are you sure you want to delete this profile?')) {
      const service = this.isAthlete ? this.athleteService : this.coachService;
      service.delete(this.profile.id.toString()).subscribe(
        () => {
          alert('Profile deleted successfully.');
          this.goBack();
        },
        (error) => {
          console.error('Error deleting profile:', error);
          alert('Error deleting profile.');
        }
      );
    }
  }

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
