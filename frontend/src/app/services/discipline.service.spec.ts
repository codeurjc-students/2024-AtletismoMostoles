import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { DisciplineService } from './discipline.service';
import { Discipline } from '../models/discipline.model';

describe('DisciplineService', () => {
  let service: DisciplineService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: spy }]
    });
    service = TestBed.inject(DisciplineService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all disciplines', () => {
    const dummyResponse = {
      content: [],
      totalElements: 0,
      totalPages: 1,
      size: 10,
      number: 0
    };

    service.getAll(0, 10, 'name').subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r => r.method === 'GET' && r.url === '/api/disciplines');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sortBy')).toBe('name');
    req.flush(dummyResponse);
  });

  it('should fetch a discipline by ID', () => {
    const dummyDiscipline: Discipline = { id: 1, name: 'Lanzamiento', description: 'Desc', coaches: [] };

    service.getById(1).subscribe(response => {
      expect(response).toEqual(dummyDiscipline);
    });

    const req = httpMock.expectOne('/api/disciplines/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyDiscipline);
  });

  it('should create a new discipline', () => {
    const newDiscipline: Discipline = { id: 1, name: 'Salto', description: 'Desc', coaches: [] };

    service.create(newDiscipline).subscribe(response => {
      expect(response).toEqual(newDiscipline);
    });

    const req = httpMock.expectOne('/api/disciplines');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newDiscipline);
    req.flush(newDiscipline);
  });

  it('should update a discipline', () => {
    const updatedDiscipline: Discipline = { id: 1, name: 'Salto Alto', description: 'Desc Updated', coaches: [] };

    service.update(1, updatedDiscipline).subscribe(response => {
      expect(response).toEqual(updatedDiscipline);
    });

    const req = httpMock.expectOne('/api/disciplines/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedDiscipline);
    req.flush(updatedDiscipline);
  });

  it('should delete a discipline', () => {
    service.delete(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('/api/disciplines/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should handle 401/403 errors and redirect to login', () => {
    service.getById(99).subscribe({
      next: () => fail('should have failed with 401 error'),
      error: error => {
        expect(error.status).toBe(401);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
          queryParams: { returnUrl: routerSpy.url }
        });
      }
    });

    const req = httpMock.expectOne('/api/disciplines/99');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
