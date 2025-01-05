import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-discipline-details',
  templateUrl: './discipline-details.component.html',
  imports: [
    FormsModule,
    RouterLink,
    NgForOf,
    RouterOutlet,
    NgIf
  ],
  standalone: true,
  styleUrls: ['./discipline-details.component.css']
})
export class DisciplineDetailsComponent implements OnInit {
  discipline: { id: number; name: string; description: string; image: string; coaches: string[] } = { id: 0, name: '', description: '', image: '', coaches: [] };
  equipmentList: { id: number; name: string }[] = [];
  allEquipment: { id: number; name: string; selected: boolean }[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  isModalOpen: boolean = false;
  isEditMode: boolean = false;
  private disciplineApiUrl: string = 'http://localhost:8080/api/disciplines';
  private equipmentApiUrl: string = 'http://localhost:8080/api/equipment';

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const disciplineId = this.route.snapshot.params['id'];
    this.loadDisciplineDetails(disciplineId);
    this.loadAllEquipment();
  }

  loadDisciplineDetails(disciplineId: number): void {
    this.http.get<{ id: number; name: string; description: string; image: string; coaches: string[] }>(`${this.disciplineApiUrl}/${disciplineId}`).subscribe(
      (data) => {
        this.discipline = data;
        this.loadEquipment(disciplineId);
      },
      (error) => {
        console.error('Error al cargar los detalles de la disciplina:', error);
      }
    );
  }

  loadEquipment(disciplineId: number): void {
    this.http.get<{ id: number; name: string }[]>(`${this.disciplineApiUrl}/${disciplineId}/equipment`).subscribe(
      (data) => {
        this.equipmentList = data;
      },
      (error) => {
        console.error('Error al cargar el equipamiento:', error);
      }
    );
  }

  loadAllEquipment(): void {
    this.http.get<{ id: number; name: string }[]>(this.equipmentApiUrl).subscribe(
      (data) => {
        this.allEquipment = data.map(item => ({ ...item, selected: false }));
      },
      (error) => {
        console.error('Error al cargar la lista de equipamiento:', error);
      }
    );
  }

  paginatedEquipment(): { id: number; name: string }[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.equipmentList.slice(startIndex, startIndex + this.itemsPerPage);
  }

  totalPages(): number {
    return Math.ceil(this.equipmentList.length / this.itemsPerPage);
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages()) {
      this.currentPage++;
    }
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  addEquipment(allEquipment: { id: number; name: string; selected: boolean }[]): void {
    const disciplineId = this.discipline?.id;
    if (disciplineId) {
      const equipmentToAdd = allEquipment.filter(e => e.selected);
      this.http.post(`${this.disciplineApiUrl}/${disciplineId}/equipment`, equipmentToAdd).subscribe(
        () => {
          alert('Equipamiento agregado correctamente');
          this.closeModal();
          this.loadEquipment(disciplineId);
        },
        (error) => {
          console.error('Error al agregar equipamiento:', error);
        }
      );
    }
  }


  toggleEditMode(): void {
    this.isEditMode = !this.isEditMode;
  }

  saveDiscipline(): void {
    if (this.discipline) {
      this.http.put(`${this.disciplineApiUrl}/${this.discipline.id}`, this.discipline).subscribe(
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

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
