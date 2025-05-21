import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RankingComponent } from './ranking.component';
import { AthleteService } from '../../services/athlete.service';
import { CoachService } from '../../services/coach.service';
import { DisciplineService } from '../../services/discipline.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { Athlete } from '../../models/athlete.model';
import { Coach } from '../../models/coach.model';
import { Discipline } from '../../models/discipline.model';
import { Page } from '../../models/page.model';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { By } from '@angular/platform-browser';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('RankingComponent', () => {
  let component: RankingComponent;
  let fixture: ComponentFixture<RankingComponent>;
  let athleteService: jasmine.SpyObj<AthleteService>;
  let coachService: jasmine.SpyObj<CoachService>;
  let disciplineService: jasmine.SpyObj<DisciplineService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  const emptyAthletePage: Page<Athlete> = { content: [], totalElements: 0, totalPages: 0, size: 0, number: 0 };
  const emptyCoachPage: Page<Coach> = { content: [], totalElements: 0, totalPages: 0, size: 0, number: 0 };
  const emptyDisciplinePage: Page<Discipline> = { content: [], totalElements: 0, totalPages: 0, size: 0, number: 0 };

  beforeEach(async () => {
    athleteService = jasmine.createSpyObj('AthleteService', ['getAll', 'getFiltered']);
    coachService = jasmine.createSpyObj('CoachService', ['getAll']);
    disciplineService = jasmine.createSpyObj('DisciplineService', ['getAll']);
    authService = jasmine.createSpyObj('AuthService', ['isAdmin', 'isAuthenticated'], { user: of(null) });

    athleteService.getAll.and.returnValue(of(emptyAthletePage));
    athleteService.getFiltered.and.returnValue(of(emptyAthletePage));
    coachService.getAll.and.returnValue(of(emptyCoachPage));
    disciplineService.getAll.and.returnValue(of(emptyDisciplinePage));

    await TestBed.configureTestingModule({
      imports: [
        RankingComponent,
        RouterTestingModule.withRoutes([]),
        MatTableModule,
        MatPaginatorModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: AthleteService, useValue: athleteService },
        { provide: CoachService, useValue: coachService },
        { provide: DisciplineService, useValue: disciplineService },
        { provide: AuthService, useValue: authService }
      ]
    }).compileComponents();

    authService.isAuthenticated.and.returnValue(true);
    authService.isAdmin.and.returnValue(true);
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(RankingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debería cargar atletas al iniciar', fakeAsync(() => {
    const mockAthletes: Athlete[] = [{ licenseNumber: 'A001', firstName: 'Juan', lastName: 'Pérez', birthDate: '1990-01-01', disciplines: [], results: [], coach: null }];
    const mockPage: Page<Athlete> = { content: mockAthletes, totalElements: 1, totalPages: 1, size: component.itemsPerPage, number: 0 };
    athleteService.getAll.and.returnValue(of(mockPage));

    component.ngOnInit();
    tick(); fixture.detectChanges();

    expect(athleteService.getAll).toHaveBeenCalledWith(0, component.itemsPerPage);
    expect(component.dataSource.data).toEqual(mockAthletes);
    expect(fixture.debugElement.queryAll(By.css('mat-row')).length).toBe(1);
  }));

  it('debería paginar correctamente usando nextPage y prevPage', fakeAsync(() => {
    const athletes = Array.from({ length: 15 }, (_, i) => ({ licenseNumber: `${i}`, firstName: '', lastName: '', birthDate: '', disciplines: [], results: [], coach: null }));
    const page0: Page<Athlete> = { content: athletes.slice(0,10), totalElements: 15, totalPages: 2, size: component.itemsPerPage, number: 0 };
    const page1: Page<Athlete> = { content: athletes.slice(10), totalElements: 15, totalPages: 2, size: component.itemsPerPage, number: 1 };
    athleteService.getAll.and.callFake((page, size) => of(page === 0 ? page0 : page1));

    component.ngOnInit(); tick();
    expect(component.totalPages).toBe(2);

    component.nextPage(); tick();
    expect(component.currentPage).toBe(2);
    expect(component.dataSource.data).toEqual(page1.content);

    component.prevPage(); tick();
    expect(component.currentPage).toBe(1);
    expect(component.dataSource.data).toEqual(page0.content);
  }));

  it('debería aplicar filtros de nombre y apellidos', fakeAsync(() => {
    component.filters.firstName = 'Ana';
    component.filters.lastName = 'López';
    const filtered: Page<Athlete> = { content: [], totalElements: 0, totalPages: 1, size: component.itemsPerPage, number: 0 };
    athleteService.getFiltered.and.returnValue(of(filtered));

    component.applyFilter();
    tick(); fixture.detectChanges();

    expect(athleteService.getFiltered).toHaveBeenCalledWith(component.filters, 0, component.itemsPerPage);
    expect(component.dataSource.data).toEqual(filtered.content);
  }));

  it('debería mostrar opciones de coach y disciplina en filtros', fakeAsync(() => {
    const mockCoaches: Coach[] = [{ id: 'C1', name: 'Coach1' } as any];
    const mockDisc: Discipline[] = [{ id: 1, name: 'Sprint', description: '' }];
    coachService.getAll.and.returnValue(of({ content: mockCoaches, totalElements:1, totalPages:1, size:1, number:0 }));
    disciplineService.getAll.and.returnValue(of({ content: mockDisc, totalElements:1, totalPages:1, size:1, number:0 }));

    component.ngOnInit(); tick(); fixture.detectChanges();

    expect(component.coaches).toEqual(mockCoaches);
    expect(component.disciplines).toEqual(mockDisc);
  }));

  it('debería navegar a detalle al hacer click en una fila', fakeAsync(() => {
    const mockAthletes: Athlete[] = [{ licenseNumber: 'A001', firstName: '', lastName: '', birthDate: '', disciplines: [], results: [], coach: null }];
    athleteService.getAll.and.returnValue(of({ content: mockAthletes, totalElements:1, totalPages:1, size:component.itemsPerPage, number:0 }));

    component.ngOnInit(); tick(); fixture.detectChanges();
    const row = fixture.debugElement.query(By.css('mat-row')); row.triggerEventHandler('click', null);

    expect(router.navigate).toHaveBeenCalledWith(['/profile', 'athlete', mockAthletes[0]]);
  }));
});
