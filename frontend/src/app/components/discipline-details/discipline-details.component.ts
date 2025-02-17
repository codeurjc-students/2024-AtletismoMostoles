import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';
import { DisciplineService } from '../../services/discipline.service';
import { EquipmentService } from '../../services/equipment.service';
import { Discipline } from '../../models/discipline.model';
import { Equipment } from '../../models/equipment.model';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-discipline-details',
  templateUrl: './discipline-details.component.html',
  styleUrls: ['./discipline-details.component.css'],
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ]
})
export class DisciplineDetailsComponent implements OnInit {
  isModalOpen: boolean = false;
  discipline: Discipline = { id: 0, name: '', description: '', imageLink: '', equipment: [], athletes: [], events: [], coaches: [] };
  allEquipment: (Equipment & { selected: boolean })[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalPages: number = 1;
  isEditMode: boolean = false;
  isLoggedIn: boolean = false;

  constructor(
    private disciplineService: DisciplineService,
    private equipmentService: EquipmentService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const disciplineId = this.route.snapshot.params['id'];
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadDisciplineDetails(disciplineId);
    this.loadAllEquipment();
  }

  loadDisciplineDetails(disciplineId: number): void {
    this.disciplineService.getById(disciplineId).subscribe(
      (data) => {
        this.discipline = data;
      },
      (error) => {
        console.error('Error al cargar los detalles de la disciplina:', error);
      }
    );
  }

  loadAllEquipment(): void {
    this.equipmentService.getAll().subscribe(
      (response) => {
        this.allEquipment = response.content.map(item => ({ ...item, selected: false }));
      },
      (error) => {
        console.error('Error al cargar la lista de equipamiento:', error);
      }
    );
  }
  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadAllEquipment();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadAllEquipment();
    }
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  addEquipment(): void {
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para editar esta disciplina.');
      return;
    }
    const selectedEquipment = this.allEquipment.filter(e => e.selected);
    if (selectedEquipment.length > 0) {
      this.discipline.equipment = [...(this.discipline.equipment || []), ...selectedEquipment];
      this.disciplineService.update(this.discipline.id, this.discipline).subscribe(
        () => {
          alert('Equipamiento agregado correctamente');
          this.closeModal();
        },
        (error) => {
          console.error('Error al agregar equipamiento:', error);
        }
      );
    } else {
      alert('Seleccione al menos un equipamiento.');
    }
  }

  toggleEditMode(): void {
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para editar esta disciplina.');
      return;
    }
    this.isEditMode = !this.isEditMode;
  }

  saveDiscipline(): void {
    if (!this.isLoggedIn) {
      alert('No tienes permiso para modificar esta disciplina.');
      return;
    }
    if (this.discipline) {
      this.disciplineService.update(this.discipline.id, this.discipline).subscribe(
        () => {
          alert('Disciplina actualizada correctamente');
          this.isEditMode = false;
        },
        (error) => {
          console.error('Error al actualizar la disciplina:', error);
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
