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
import { AddResultsDialogComponent } from '../../modals/AddResultDialogComponent';
import { ResultService } from '../../services/result.service';
import { Athlete } from '../../models/athlete.model';
import { AthleteService } from '../../services/athlete.service';
import { forkJoin, map } from 'rxjs';
import { DisciplineService } from '../../services/discipline.service';
import { Results } from '../../models/results.model';


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
  paginatedResults: Results[] = [];
  currentPage: number = 1;
  itemsPerPage = 10;
  totalPages: number = 1;
  mapUrl: string = '';
  sanitizedMapUrl: SafeResourceUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventService: EventService,
    private authService: AuthService,
    private athleteService: AthleteService,
    private disciplineService: DisciplineService,
    private fb: FormBuilder,
    public dialog: MatDialog,
    private sanitizer: DomSanitizer,
    private resultService: ResultService,
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
      this.eventService.getById(eventId).subscribe(eventResponse => {
        this.event = eventResponse;
        this.mapUrl = eventResponse.mapLink || '';
        this.sanitizedMapUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.mapUrl);

        this.eventForm.patchValue({
          name: this.event.name,
          date: this.event.date,
          isOrganizedByClub: this.event.organizedByClub,
          imageLink: this.event.imageLink
        });

        // ✅ Ahora sí: usamos el service de resultados directamente
        this.resultService.getByEventId(eventId).subscribe({
          next: (page) => {
            const rawResults = page.content;

            if (!rawResults || rawResults.length === 0) {
              this.results = [];
              return;
            }

            const enrichment$ = rawResults.map(result =>
              forkJoin({
                athlete: this.athleteService.getById(result.atletaId).pipe(
                  map(a => ({
                    firstName: a?.firstName || 'Desconocido',
                    lastName: a?.lastName || ''
                  }))
                ),
                discipline: this.disciplineService.getById(result.disciplinaId).pipe(
                  map(d => ({ name: d?.name || 'Sin disciplina' }))
                )
              }).pipe(
                map(({ athlete, discipline }) => ({
                  ...result,
                  athlete,
                  discipline
                }))
              )
            );

            forkJoin(enrichment$).subscribe({
              next: enriched => {
                this.results = enriched;
                this.totalPages = Math.ceil(this.results.length / this.itemsPerPage);
                this.updatePagination();
                console.log('Resultados enriquecidos:', this.results);
              },
              error: err => {
                console.error('Error enriqueciendo resultados:', err);
                this.results = rawResults; // fallback
              }
            });
          },
          error: (err) => {
            console.error('Error cargando resultados:', err);
            this.results = [];
          }
        });
      });
    }
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
  }

  saveEvent(): void {
    if (this.eventForm.valid) {
      const updatedEvent = {
        ...this.event,
        ...this.eventForm.value  // toma los valores del form directamente
      };

      console.log('Datos enviados a la API:', updatedEvent);

      this.eventService.update(updatedEvent.id, updatedEvent).subscribe(() => {
        alert('Evento actualizado con éxito');
        this.isEditing = false;
        this.loadEvent(); // recarga datos actualizados
      });
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

  openAddResultsDialog(): void {
    this.athleteService.getAll(0, 1000).subscribe((response) => {
      const athletes: Athlete[] = response.content;

      this.dialog.open(AddResultsDialogComponent, {
        width: '600px',
        data: {
          eventId: this.event.id,
          disciplines: this.event.disciplines || [],
          athletes: athletes
        }
      }).afterClosed().subscribe((resultsToSave: any[]) => {
        if (resultsToSave?.length) {
          this.resultService.createMultiple(resultsToSave).subscribe(() => {
            alert('Resultados agregados correctamente');
            this.loadEvent();
          }, error => {
            console.error('Error al guardar resultados:', error);
            alert('Error al guardar resultados');
          });
        }
      });
    });
  }

  updatePagination(): void {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = this.currentPage * this.itemsPerPage;
    this.paginatedResults = this.results.slice(start, end);
  }
}
