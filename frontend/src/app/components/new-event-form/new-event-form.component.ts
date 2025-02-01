import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { Discipline } from '../../models/discipline.model';
import { NgForOf, NgIf } from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import { EventService } from '../../services/event.service';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-new-event-form',
  templateUrl: './new-event-form.component.html',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    NgForOf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./new-event-form.component.css']
})
export class NewEventFormComponent implements OnInit {
  eventForm: FormGroup;
  disciplines: Discipline[] = [];
  errorMessage: string = '';
  isLoggedIn: boolean = false;

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private disciplineService: DisciplineService,
    private router: Router,
    private authService: AuthService
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
    console.log("isLoggedIn:",this.authService.isAuthenticated());
    if (!this.isLoggedIn) {
      alert('Debes iniciar sesión para crear un evento.');
      this.router.navigate(['/login']);
    }

    if (!this.authService.isAdmin() && !this.authService.getCurrentUser()?.roles.includes('USER')) {
      alert('No tienes permisos para crear eventos.');
      this.router.navigate(['/eventos']); // Redirigir a la lista de eventos
      return;
    }

    console.log("llegas hasta el new event");
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.disciplineService.getAll(0, 100, 'name').subscribe(
      (response) => {
        this.disciplines = response.content;
      },
      (error) => {
        console.error('Error al cargar las disciplinas', error);
        this.errorMessage = 'Error al cargar las disciplinas. Inténtalo de nuevo más tarde.';
      }
    );
  }

  onSubmit(): void {
    if (!this.isLoggedIn) {
      alert('No tienes permiso para crear eventos.');
      return;
    }
    if (this.eventForm.invalid) {
      this.errorMessage = 'Por favor, rellene el formulario correctamente.';
      return;
    }

    const formValue = this.eventForm.value;
    const newEvent = {
      ...formValue,
      disciplines: formValue.disciplines.map((id: number) => ({ id }))
    };

    this.eventService.create(newEvent).subscribe(
      () => {
        alert('Evento creado correctamente!');
        this.router.navigate(['/events']);
      },
      (error) => {
        console.error('Error creando evento:', error);
        this.errorMessage = 'Error creando evento. Intentalo de nuevo.';
      }
    );
  }

  onCancel(): void {
    if (confirm('Quieres cancelar la creacion de este evento?')) {
      this.router.navigate(['/events']);
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.eventForm.get(controlName);
    return control ? control.invalid && control.dirty : false;
  }

  // Helper methods to get form controls
  get name() {
    return this.eventForm.get('name');
  }

  get imageUrl() {
    return this.eventForm.get('imageUrl');
  }

  get mapUrl() {
    return this.eventForm.get('mapUrl');
  }

  get date() {
    return this.eventForm.get('date');
  }

  get organizedByClub() {
    return this.eventForm.get('organizedByClub');
  }

  get selectedDisciplines() {
    return this.eventForm.get('disciplines');
  }
}
