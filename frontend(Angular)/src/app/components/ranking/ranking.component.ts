import { Component, OnInit } from '@angular/core';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Discipline } from '../../models/discipline.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-ranking',
  templateUrl: './ranking.component.html',
  imports: [
    NgIf,
    NgForOf,
    RouterOutlet,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    HttpClientModule
  ],
  standalone: true,
  styleUrls: ['./ranking.component.css']
})
export class RankingComponent implements OnInit {
  filters = {firstName: '', lastName: '', discipline: '', licenseNumber: '', coach: ''};
  paginatedAtletas: Athlete[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  coaches: Coach[] = [];
  disciplines: Discipline[] = [];
  athleteForm: FormGroup;
  isAdmin: boolean = false;

  constructor(
    private athleteService: AthleteService,
    private coachService: CoachService,
    private disciplineService: DisciplineService,
    private modalService: NgbModal,
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.athleteForm = this.fb.group({
      licenseNumber: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      birthDate: ['', Validators.required],
      coach: ['', Validators.required],
      disciplines: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isAdmin = this.authService.isAdmin();
    });
    this.loadAtletas();
    this.loadCoaches();
    this.loadDisciplines();
  }

  loadAtletas(): void {
    this.athleteService.getAll(this.currentPage - 1, this.itemsPerPage).subscribe(
      response => {
        this.paginatedAtletas = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al cargar atletas:', error);
      }
    );
  }

  loadCoaches(): void {
    this.coachService.getAll().subscribe(
      response => {
        this.coaches = response.content;
      },
      error => {
        console.error('Error al cargar entrenadores:', error);
      }
    );
  }

  loadDisciplines(): void {
    this.disciplineService.getAll().subscribe(
      response => {
        this.disciplines = response.content;
      },
      error => {
        console.error('Error al cargar disciplinas:', error);
      }
    );
  }

  applyFilter(): void {
    this.currentPage = 1;
    this.athleteService.getFiltered(this.filters, this.currentPage - 1, this.itemsPerPage).subscribe(
      response => {
        this.paginatedAtletas = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al filtrar atletas:', error);
      }
    );
  }


  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadAtletas();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadAtletas();
    }
  }

  formatDisciplines(disciplines: Discipline[] | undefined): string {
    return disciplines ? disciplines.map(d => d.name).join(', ') : '';
  }

  openNewAthleteModal(content: any): void {
    if (!this.isAdmin) {
      alert('Solo los administradores pueden crear atletas.');
      return;
    }
    this.athleteForm.reset();
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then(
      result => {
        if (result === 'Save') {
          this.createAthlete();
        }
      },
      reason => {
        console.log('Modal dismissed:', reason);
      }
    );
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
        disciplines: disciplines
      };

      this.athleteService.create(newAthlete).subscribe(
        response => {
          alert('Atleta creado exitosamente');
          this.loadAtletas();
        },
        error => {
          console.error('Error al crear atleta:', error);
          alert('Error al crear atleta');
        }
      );
    } else {
      alert('Por favor, complete todos los campos requeridos');
    }
  }


  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
