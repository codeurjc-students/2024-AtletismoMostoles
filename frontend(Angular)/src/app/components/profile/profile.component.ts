import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {NgForOf, NgIf} from '@angular/common';

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
  nombre: string;
  apellido: string;
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
  athletes: Athlete[] = [];
  paginatedAthletes: Athlete[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  isAthlete = true;

  private apiUrl = 'http://localhost:8080/api';

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}

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
    const endpoint = this.isAthlete ? 'athletes' : 'coaches';
    this.http.get<Profile>(`${this.apiUrl}/${endpoint}/${id}`).subscribe(
      (response) => {
        this.profile = response;
      },
      (error) => {
        console.error('Error loading profile:', error);
      }
    );
  }

  loadResults(id: number): void {
    this.http.get<Result[]>(`${this.apiUrl}/athletes/${id}/results`).subscribe(
      (response) => {
        this.results = response;
        this.totalPages = Math.ceil(this.results.length / this.itemsPerPage);
        this.updatePagination('results');
      },
      (error) => {
        console.error('Error loading results:', error);
      }
    );
  }

  loadAthletes(id: number): void {
    this.http.get<Athlete[]>(`${this.apiUrl}/coaches/${id}/athletes`).subscribe(
      (response) => {
        this.athletes = response;
        this.totalPages = Math.ceil(this.athletes.length / this.itemsPerPage);
        this.updatePagination('athletes');
      },
      (error) => {
        console.error('Error loading athletes:', error);
      }
    );
  }

  updatePagination(type: 'results' | 'athletes'): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    if (type === 'results') {
      this.paginatedResults = this.results.slice(start, end);
    } else {
      this.paginatedAthletes = this.athletes.slice(start, end);
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
      const endpoint = this.isAthlete ? 'athletes' : 'coaches';
      this.http.delete(`${this.apiUrl}/${endpoint}/${this.profile.id}`).subscribe(
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
