import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpClientModule, HttpParams} from '@angular/common/http';
import {ActivatedRoute, Router, RouterLink, RouterOutlet} from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgForOf, NgIf } from '@angular/common';



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
  discipline: { id: number; name: string; description: string; image: string; coaches: { licenseNumber: string; firstName: string; lastName: string }[] } = { id: 0, name: '', description: '', image: '', coaches: [] };
  equipmentList: { id: number; name: string }[] = [];
  allEquipment: { id: number; name: string; selected: boolean }[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  totalPages: number = 1;
  private disciplineApiUrl: string = 'http://localhost:8080/api/disciplines';
  private equipmentApiUrl: string = 'http://localhost:8080/api/equipment';

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const disciplineId = this.route.snapshot.params['id'];
    this.loadDisciplineDetails(disciplineId);
    this.loadAllEquipment();
  }

  loadDisciplineDetails(disciplineId: number): void {
    this.http.get<{ id: number; name: string; description: string; image: string; coaches: { licenseNumber: string; firstName: string; lastName: string }[] }>(`${this.disciplineApiUrl}/${disciplineId}`).subscribe(
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
    const params = new HttpParams()
      .set('page', (this.currentPage - 1).toString())
      .set('size', this.itemsPerPage.toString());

    this.http.get<any>(`${this.disciplineApiUrl}/${disciplineId}/equipment`, { params }).subscribe(
      (response) => {
        this.equipmentList = response.content;
        this.totalPages = response.totalPages;
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

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.loadEquipment(this.discipline.id);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.loadEquipment(this.discipline.id);
    }
  }

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  addEquipment(): void {
    const disciplineId = this.discipline?.id;
    if (disciplineId) {
      const equipmentToAdd = this.allEquipment.filter(e => e.selected);
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

  isEditMode: boolean = false;

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
