import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { EventService } from '../../services/event.service';
import { DisciplineService } from '../../services/discipline.service';
import { Discipline } from '../../models/discipline.model';
import { NgForOf, NgIf } from '@angular/common';
import {HttpClientModule} from '@angular/common/http';

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

  constructor(
    private fb: FormBuilder,
    private eventService: EventService,
    private disciplineService: DisciplineService,
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
    this.loadDisciplines();
  }

  loadDisciplines(): void {
    this.disciplineService.getAll(0, 100, 'name').subscribe(
      (response) => {
        this.disciplines = response.content;
      },
      (error) => {
        console.error('Error loading disciplines:', error);
        this.errorMessage = 'Error loading disciplines. Please try again later.';
      }
    );
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      this.errorMessage = 'Please fill out the form correctly.';
      return;
    }

    const formValue = this.eventForm.value;
    const newEvent = {
      ...formValue,
      disciplines: formValue.disciplines.map((id: number) => ({ id }))
    };

    this.eventService.create(newEvent).subscribe(
      () => {
        alert('Event created successfully!');
        this.router.navigate(['/events']);
      },
      (error) => {
        console.error('Error creating event:', error);
        this.errorMessage = 'Error creating event. Please try again.';
      }
    );
  }

  onCancel(): void {
    if (confirm('Are you sure you want to cancel creating the event?')) {
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
