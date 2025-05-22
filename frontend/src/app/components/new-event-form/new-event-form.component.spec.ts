import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NewEventFormComponent } from './new-event-form.component';
import { DisciplineService } from '../../services/discipline.service';
import { EventService } from '../../services/event.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Discipline } from '../../models/discipline.model';
import { EventCreate } from '../../models/event-create.model';
import { Event } from '../../models/event.model';
import { Page } from '../../models/page.model';

describe('NewEventFormComponent', () => {
  let component: NewEventFormComponent;
  let fixture: ComponentFixture<NewEventFormComponent>;
  let disciplineService: jasmine.SpyObj<DisciplineService>;
  let eventService: jasmine.SpyObj<EventService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  const makeDisciplinePage = (content: Discipline[]): Page<Discipline> => ({
    content,
    totalElements: content.length,
    totalPages: 1,
    size: content.length,
    number: 0
  });

  beforeEach(async () => {
    disciplineService = jasmine.createSpyObj('DisciplineService', ['getAll']);
    eventService = jasmine.createSpyObj('EventService', ['create']);
    authService = jasmine.createSpyObj('AuthService', ['isAuthenticated']);

    disciplineService.getAll.and.returnValue(of(makeDisciplinePage([])));

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule,
        NewEventFormComponent
      ],
      providers: [
        { provide: DisciplineService, useValue: disciplineService },
        { provide: EventService, useValue: eventService },
        { provide: AuthService, useValue: authService }
      ]
    }).compileComponents();

    TestBed.overrideComponent(NewEventFormComponent, {
      set: { template: '' }
    });

    fixture = TestBed.createComponent(NewEventFormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit', () => {
    it('redirecciona al login si no está autenticado', () => {
      authService.isAuthenticated.and.returnValue(false);
      spyOn(window, 'alert').and.stub();
      spyOn(router, 'navigate');

      component.ngOnInit();

      expect(window.alert).toHaveBeenCalledWith('Debes iniciar sesión para crear un evento.');
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('carga disciplinas si está autenticado', () => {
      const mockDiscs: Discipline[] = [{ id: 1, name: 'X', description: 'Y' }];
      authService.isAuthenticated.and.returnValue(true);
      disciplineService.getAll.and.returnValue(of(makeDisciplinePage(mockDiscs)));

      component.ngOnInit();

      expect(disciplineService.getAll).toHaveBeenCalledWith(0, 100, 'name');
      expect(component.disciplines).toEqual(mockDiscs);
    });

    it('muestra mensaje de error si falla la carga de disciplinas', () => {
      authService.isAuthenticated.and.returnValue(true);
      disciplineService.getAll.and.returnValue(throwError(() => new Error('fail')));
      component.errorMessage = '';

      component.ngOnInit();

      expect(component.errorMessage).toBe('Error al cargar disciplinas. Intenta más tarde.');
    });
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      authService.isAuthenticated.and.returnValue(true);
      const mockDiscs: Discipline[] = [{ id: 1, name: 'X', description: 'Y' }];
      disciplineService.getAll.and.returnValue(of(makeDisciplinePage(mockDiscs)));
      component.ngOnInit();
    });

    it('muestra error si el formulario es inválido', () => {
      component.eventForm.controls['name'].setValue('');
      component.onSubmit();
      expect(component.errorMessage).toBe('Por favor, rellena el formulario correctamente.');
      expect(eventService.create).not.toHaveBeenCalled();
    });

    it('llama a create y navega tras enviar un formulario válido', () => {
      component.eventForm.setValue({
        name: 'Evt1',                   // ≥3 caracteres para pasar Validators.minLength(3) :contentReference[oaicite:0]{index=0}
        imageUrl: 'http://i',
        mapUrl: 'http://m',
        date: '2025-01-01',
        organizedByClub: true,
        disciplines: [1]
      });

      const fakeEvent: Event = {
        id: 1,
        name: 'Evt1',
        date: '2025-01-01',
        organizedByClub: true,
        imageLink: 'http://i',
        mapLink: 'http://m',
        disciplines: [{ id: 1, name: 'X', description: 'Y' }],
        results: []
      };
      eventService.create.and.returnValue(of(fakeEvent));
      spyOn(window, 'alert').and.stub();
      spyOn(router, 'navigate');

      component.onSubmit();

      const expectedPayload: EventCreate = {
        name: 'Evt1',
        imageUrl: 'http://i',
        mapUrl: 'http://m',
        date: '2025-01-01',
        isOrganizedByClub: true,
        disciplines: [{ id: 1 }]
      };
      expect(eventService.create).toHaveBeenCalledWith(expectedPayload);
      expect(window.alert).toHaveBeenCalledWith('Evento creado correctamente');
      expect(router.navigate).toHaveBeenCalledWith(['/eventos']);
    });

    it('muestra mensaje de error si falla la creación', () => {
      component.eventForm.setValue({
        name: 'Evt1',
        imageUrl: 'http://i',
        mapUrl: 'http://m',
        date: '2025-01-01',
        organizedByClub: false,
        disciplines: [1]
      });
      eventService.create.and.returnValue(throwError(() => new Error('fail')));

      component.onSubmit();
      expect(component.errorMessage).toBe('Error creando evento, inténtalo más tarde.');
    });
  });

  describe('onCancel', () => {
    it('navega si confirma cancelación', () => {
      spyOn(window, 'confirm').and.returnValue(true);
      spyOn(router, 'navigate');

      component.onCancel();
      expect(router.navigate).toHaveBeenCalledWith(['/eventos']);
    });

    it('no navega si cancela', () => {
      spyOn(window, 'confirm').and.returnValue(false);
      spyOn(router, 'navigate');

      component.onCancel();
      expect(router.navigate).not.toHaveBeenCalled();
    });
  });

  describe('helpers de formulario', () => {
    it('isInvalid refleja el estado del control', () => {
      const ctrl = component.eventForm.get('name')!;
      ctrl.setValue('');
      ctrl.markAsTouched();
      expect(component.isInvalid('name')).toBeTrue();

      ctrl.setValue('XYZ');
      expect(component.isInvalid('name')).toBeFalse();
    });

    it('isFormInvalid getter funciona', () => {
      component.eventForm.get('name')!.setValue('');
      expect(component.isFormInvalid).toBeTrue();
    });
  });
});
