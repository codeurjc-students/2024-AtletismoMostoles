import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import { EventService } from '../../services/event.service';
import { DisciplineService } from '../../services/discipline.service';
import {NgForOf, NgIf} from '@angular/common';


@Component({
  selector: 'app-new-event-form',
  templateUrl: './new-event-form.component.html',
  standalone: true,
  imports: [
    NgIf,
    ReactiveFormsModule,
    NgForOf,
    RouterOutlet,
    RouterLink,
  ],
  styleUrls: ['./new-event-form.component.css']
})
export class NewEventFormComponent implements OnInit {
  eventForm: FormGroup;
  disciplines: string[] = [];
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
    this.disciplineService.getAll().subscribe(
      (response) => {
        this.disciplines = response;
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

    this.eventService.create(this.eventForm.value).subscribe(
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

  toggleMenu(): void {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
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
