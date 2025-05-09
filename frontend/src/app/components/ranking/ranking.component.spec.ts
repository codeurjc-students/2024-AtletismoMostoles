import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RankingComponent } from './ranking.component';
import { AthleteService } from '../../services/athlete.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { Athlete } from '../../models/athlete.model';
import { Page } from '../../models/page.model';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RankingComponent', () => {
  let component: RankingComponent;
  let fixture: ComponentFixture<RankingComponent>;
  let athleteServiceSpy: jasmine.SpyObj<AthleteService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    athleteServiceSpy = jasmine.createSpyObj('AthleteService', ['getFiltered']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        RankingComponent,  // Standalone component
        RouterTestingModule.withRoutes([]),
        MatTableModule,
        MatPaginatorModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AthleteService, useValue: athleteServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RankingComponent);
    component = fixture.componentInstance;
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar atletas al iniciar', fakeAsync(() => {
    const mockAthletes: Athlete[] = [
      {
        licenseNumber: 'A001',
        firstName: 'Juan',
        lastName: 'Pérez',
        birthDate: '1990-01-01',
        disciplines: [],
        results: [],
        coach: null
      }
    ];
    const mockPage: Page<Athlete> = {
      content: mockAthletes,
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0
    };

    athleteServiceSpy.getFiltered.and.returnValue(of(mockPage));

    component.ngOnInit();
    tick();
    fixture.detectChanges();

    expect(athleteServiceSpy.getFiltered).toHaveBeenCalled();
    expect(component.dataSource.data).toEqual(mockAthletes);

    const rows = fixture.debugElement.queryAll(By.css('table mat-row'));
    expect(rows.length).toBe(1);  // integración ligera: la tabla tiene 1 fila
  }));

  it('debería aplicar filtros correctamente', fakeAsync(() => {
    const pageMock: Page<Athlete> = {
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    };

    athleteServiceSpy.getFiltered.and.returnValue(of(pageMock));

    // Actualiza los filtros directamente en el componente
    component.filters.firstName = 'Ana';
    component.filters.lastName = 'López';

    component.applyFilter();
    tick();

    expect(athleteServiceSpy.getFiltered).toHaveBeenCalledWith(
      component.filters, 0, component.itemsPerPage
    );
  }));

  it('debería paginar correctamente', fakeAsync(() => {
    component.totalPages = 3;
    component.currentPage = 1;

    component.nextPage();
    expect(component.currentPage).toBe(2);

    component.prevPage();
    expect(component.currentPage).toBe(1);

    // No debe pasar de la última página
    component.currentPage = 3;
    component.nextPage();
    expect(component.currentPage).toBe(3);

    // No debe bajar de la primera página
    component.currentPage = 1;
    component.prevPage();
    expect(component.currentPage).toBe(1);
  }));

  it('debería formatear disciplinas correctamente', () => {
    const athlete: Athlete = {
      licenseNumber: 'A002',
      firstName: 'Laura',
      lastName: 'Gómez',
      birthDate: '1992-02-02',
      disciplines: [
        { id: 1, name: 'Salto', description: 'Salto largo' },
        { id: 2, name: 'Velocidad', description: '100m lisos' }
      ],
      results: [],
      coach: null
    };

    const result = component.formatDisciplines(athlete.disciplines!);
    expect(result).toBe('Salto, Velocidad');
  });

  it('debería navegar al perfil del atleta', () => {
    const athlete: Athlete = {
      licenseNumber: 'A003',
      firstName: 'Pedro',
      lastName: 'Martínez',
      birthDate: '1988-03-03',
      disciplines: [],
      results: [],
      coach: null
    };

    component.goToAthleteProfile(athlete.licenseNumber);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/profile', 'athlete', athlete.licenseNumber]);
  });
});
