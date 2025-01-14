import { Component, OnInit } from '@angular/core';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { Coach } from '../../models/coach.model';
import { Discipline } from '../../models/discipline.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';

@Component({
  selector: 'app-clubmembers',
  templateUrl: './clubmembers.component.html',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    RouterLink,
    ReactiveFormsModule,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./clubmembers.component.css']
})
export class ClubMembersComponent implements OnInit {
  filters = { firstName: '', lastName: '', licenseNumber:'', discipline:'' };
  coaches: Coach[] = [];
  disciplines: Discipline[] = [];
  currentPage = 1;
  itemsPerPage = 10;
  totalPages = 1;
  coachForm: FormGroup;

  constructor(
    private coachService: CoachService,
    private disciplineService: DisciplineService,
    private modalService: NgbModal,
    private fb: FormBuilder
  ) {
    this.coachForm = this.fb.group({
      licenseNumber: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      disciplines: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCoaches();
    this.loadDisciplines();
  }

  loadCoaches(): void {
    this.coachService.getAll(this.currentPage - 1, this.itemsPerPage).subscribe(
      response => {
        this.coaches = response.content;
        this.totalPages = response.totalPages;
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

  applyFilters(): void {
    this.currentPage = 1;
    this.coachService.getFiltered(this.filters, this.currentPage - 1, this.itemsPerPage).subscribe(
      response => {
        this.coaches = response.content;
        this.totalPages = response.totalPages;
      },
      error => {
        console.error('Error al filtrar entrenadores:', error);
      }
    );
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

  openNewCoachModal(content: any): void {
    this.coachForm.reset();
    this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then(
      result => {
        if (result === 'Save') {
          this.createCoach();
        }
      },
      reason => {
        console.log('Modal dismissed:', reason);
      }
    );
  }

  createCoach(): void {
    if (this.coachForm.valid) {
      const formValue = this.coachForm.value;
      const disciplines = formValue.disciplines.map((id: number) => ({ id }));

      const newCoach: Coach = {
        ...formValue,
        disciplines: disciplines
      };

      this.coachService.create(newCoach).subscribe(
        response => {
          alert('Entrenador creado exitosamente');
          this.loadCoaches();
        },
        error => {
          console.error('Error al crear entrenador:', error);
          alert('Error al crear entrenador');
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
