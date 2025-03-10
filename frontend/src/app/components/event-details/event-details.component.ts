import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { CommonModule, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgIf,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule,
    MatDialogModule,
    MatSelectModule,
    MatToolbarModule,
    MatCheckboxModule,
    MatMenuModule,
    RouterLink,
    RouterOutlet
  ],
})
export class EventDetailsComponent implements OnInit {
  event: any;
  isLoggedIn: boolean = false;
  isEditing: boolean = false;
  eventForm: FormGroup;
  results: any[] = [];
  currentPage: number = 1;
  totalPages: number = 1;
  mapUrl: string = '';
  sanitizedMapUrl: SafeResourceUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private authService: AuthService,
    private fb: FormBuilder,
    public dialog: MatDialog,
    private sanitizer: DomSanitizer
  ) {
    this.eventForm = this.fb.group({
      name: ['', Validators.required],
      date: ['', Validators.required],
      isOrganizedByClub: [false],
      imageLink: ['']
    });
  }

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.isLoggedIn = this.authService.isAuthenticated();
    });
    this.loadEvent();
  }

  loadEvent(): void {
    const eventId = Number(this.route.snapshot.paramMap.get('id'));
    if (eventId) {
      this.eventService.getById(eventId).subscribe(response => {
        this.event = response;
        this.results = response.results || [];
        this.mapUrl = response.mapLink || '';
        this.sanitizedMapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.mapUrl);
        this.eventForm.patchValue({
          name: this.event.name,
          date: this.event.date,
          isOrganizedByClub: this.event.isOrganizedByClub,
          imageLink: this.event.imageLink
        });
      });
    }
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
  }

  saveEvent(): void {
    if (this.eventForm.valid) {
      this.event = {
        ...this.event,
        ...this.eventForm.getRawValue()
      };

      console.log('Datos enviados a la API:', this.event);

      if (this.eventForm.valid) {
        this.eventService.update(this.event.id, {
          ...this.event,
          isOrganizedByClub: !!this.event.isOrganizedByClub
        }).subscribe(() => {
          alert('Evento actualizado con éxito');
          this.isEditing = false;
          this.loadEvent();
        });
      }
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.authService.logout();
  }

}
