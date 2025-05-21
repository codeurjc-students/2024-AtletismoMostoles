import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule, NgForOf, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { EquipmentDialogComponent } from '../../modals/EquipmentDialogComponent.modal';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-discipline-details',
  templateUrl: './discipline-details.component.html',
  styleUrls: ['./discipline-details.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatDialogModule,
    MatMenuModule,
    RouterLink,
    RouterOutlet
  ],
})
export class DisciplineDetailsComponent implements OnInit {
  discipline: any;
  isLoggedIn: boolean = false;
  isEditMode: boolean = false;
  disciplineForm: FormGroup;
  currentPage: number = 1;
  totalPages: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private disciplineService: DisciplineService,
    private authService: AuthService,
    private fb: FormBuilder,
    public dialog: MatDialog
  ) {
    this.disciplineForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadDiscipline();
  }

  loadDiscipline(): void {
    const disciplineId = Number(this.route.snapshot.paramMap.get('id'));
    if (disciplineId) {
      this.disciplineService.getById(Number(disciplineId)).subscribe(response => {
        this.discipline = response;
        this.disciplineForm.patchValue({
          name: this.discipline.name,
          description: this.discipline.description
        });
      });
    }
  }

  toggleEditMode(): void {
    this.isEditMode = !this.isEditMode;
  }

  saveDiscipline(): void {
    if (this.disciplineForm.valid) {
      this.disciplineService.update(this.discipline.id, this.disciplineForm.value).subscribe(() => {
        alert('Disciplina actualizada con éxito');
        this.isEditMode = false;
        this.loadDiscipline();
      });
    } else {
      alert('Por favor, completa el formulario correctamente.');
    }
  }


  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  openEquipmentDialog(): void {
    const dialogRef = this.dialog.open(EquipmentDialogComponent, {
      width: '400px',
      data: { equipment: this.discipline?.equipment ?? [] }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (
        result &&
        typeof result === 'object' &&
        this.discipline &&
        Array.isArray(this.discipline.equipment)
      ) {
        this.discipline.equipment.push(result);
        this.loadDiscipline();
      }
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }

}
