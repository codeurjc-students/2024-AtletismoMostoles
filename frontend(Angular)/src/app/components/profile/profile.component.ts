import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Results } from '../../models/results.model';
import {Observable} from 'rxjs';
import {HttpClientModule} from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Discipline } from '../../models/discipline.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    RouterLink,
    RouterOutlet,
    DatePipe,
    HttpClientModule,
    ReactiveFormsModule
  ],
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile!: Athlete | Coach;
  results: Results[] = [];
  paginatedResults: Results[] = [];
  paginatedAthletes: Athlete[] = [];
  disciplines: Discipline[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  profileForm!: FormGroup;
  isEditing: boolean = false;
  isAthlete = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private athleteService: AthleteService,
    private coachService: CoachService,
    private fb: FormBuilder,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    const type = this.route.snapshot.params['type'];
    const id = this.route.snapshot.params['id'];
    this.isAthlete = type === 'athlete';
    this.loadProfile(id);
  }

  loadProfile(id: number): void {
    const service = this.isAthlete ? this.athleteService : this.coachService;
    (service.getById(id.toString()) as Observable<Athlete | Coach>).subscribe(
      (response) => {
        this.profile = response;
        if (this.isAthlete) {
          this.results = (response as Athlete).results || [];
          this.totalPages = Math.ceil(this.results.length / this.itemsPerPage);
          this.updatePagination('results');
        } else {
          this.paginatedAthletes = (response as Coach).athletes || [];
          this.totalPages = Math.ceil(this.paginatedAthletes.length / this.itemsPerPage);
          this.updatePagination('athletes');
        }
      },
      (error: any) => {
        console.error('Error loading profile:', error);
        this.errorMessage = 'Error loading profile. Please try again later.';
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
    if(this.isAthlete){
      this.router.navigate(['/ranking']);
    }else {
      this.router.navigate(['/miembros']);
    }
  }
  enableEdit(): void {
    this.isEditing = true;

    const disciplines = this.profile.disciplines?.map(d => d.id); // Extraer IDs de disciplinas

    this.profileForm = this.fb.group({
      licenseNumber: [this.profile.licenseNumber, Validators.required],
      firstName: [this.profile.firstName, Validators.required],
      lastName: [this.profile.lastName, Validators.required],
      birthDate: [this.isAthlete ? (this.profile as Athlete).birthDate : '', Validators.required],
      disciplines: [disciplines, Validators.required]
    });
  }


  deleteProfile(): void {
    if (confirm('Are you sure you want to delete this profile?')) {
      const service = this.isAthlete ? this.athleteService : this.coachService;
      service.delete(this.profile.licenseNumber).subscribe(
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

  saveProfile(): void {
    if (this.profileForm.valid) {
      const updatedProfile = {
        ...this.profile,
        ...this.profileForm.value,
        disciplines: this.profileForm.value.disciplines.map((id: number) => ({ id }))
      };

      if (this.isAthlete) {
        this.athleteService.update(updatedProfile.licenseNumber, updatedProfile).subscribe(
          () => {
            alert('Profile updated successfully.');
            this.isEditing = false;
            this.loadProfile(updatedProfile.licenseNumber);
          },
          (error: any) => {
            console.error('Error updating profile:', error);
            alert('Error updating profile.');
          }
        );
      } else {
        this.coachService.update(updatedProfile.licenseNumber, updatedProfile).subscribe(
          () => {
            alert('Profile updated successfully.');
            this.isEditing = false;
            this.loadProfile(updatedProfile.licenseNumber);
          },
          (error: any) => {
            console.error('Error updating profile:', error);
            alert('Error updating profile.');
          }
        );
      }
    } else {
      alert('Por favor, complete todos los campos requeridos.');
    }
  }



  cancelEdit(): void {
    this.isEditing = false;
  }
  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }

  get coach() {
    return this.isAthlete ? (this.profile as Athlete).coach : undefined;
  }

  get birthDate() {
    return this.isAthlete ? (this.profile as Athlete).birthDate : undefined;
  }
}
