import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup } from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { Discipline } from '../../models/discipline.model';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { EventService } from '../../services/event.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { EventCreate } from '../../models/event-create.model';
import { MatMenuModule } from '@angular/material/menu';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';


@Component({
  selector: 'app-new-event-form',
  templateUrl: './new-event-form.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatCheckboxModule,
    MatSelectModule,
    MatMenuModule,
    MatIconModule,
    MatCardModule,
    RouterLink,
    RouterOutlet,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./new-event-form.component.css']
})
export class NewEventFormComponent implements OnInit {
  eventForm: FormGroup;
  disciplines: Discipline[] = [];
  errorMessage: string = '';
  isLoggedIn: boolean = false;

  constructor(
    private disciplineService: DisciplineService,
    private eventService: EventService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.eventForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      imageUrl: ['', [Validators.required, Validators.pattern(/https?:\/\/.+/)]],
      mapUrl: ['', [Validators.required, Validators.pattern(/https?:\/\/.+/)]],
      date: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
      organizedByClub: [false],
      disciplines: [[], Validators.required]
    });
  }

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();

    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para crear un evento.');
      this.router.navigate(['/login']);
    }

    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.disciplineService.getAll(0, 100, 'name').subscribe({
      next: (response) => (this.disciplines = response.content),
      error: (error) => {
        console.error('Error al cargar las disciplinas:', error);
        this.errorMessage = 'Error al cargar disciplinas. Intenta más tarde.';
      }
    });
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      this.errorMessage = 'Por favor, rellena el formulario correctamente.';
      return;
    }

    const formValue = this.eventForm.value;

    const newEvent:EventCreate = {
      name: formValue.name!,
      imageLink: formValue.imageUrl!,
      mapLink: formValue.mapUrl!,
      date: formValue.date!,
      organizedByClub: formValue.organizedByClub || false,
      disciplineIds: formValue.disciplines!.map((id: number) =>  id )
    };

    this.eventService.create(newEvent).subscribe({
      next: () => {
        //alert('Evento creado correctamente');
        this.router.navigate(['/eventos']);
      },
      error: (error) => {
        console.error('Error al crear evento:', error);
        this.errorMessage = 'Error creando evento, inténtalo más tarde.';
      }
    });
  }


  onCancel(): void {
    if (confirm('¿Quieres cancelar la creación de este evento?')) {
      this.router.navigate(['/eventos']);
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.eventForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }


  login() {
    this.router.navigate(['/login']);
  }

  logout(){
    this.authService.logout();
  }

  get isFormInvalid(): boolean {
    return this.eventForm.invalid;
  }

}
