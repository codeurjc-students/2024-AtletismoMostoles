import { TestBed } from '@angular/core/testing';
import { AthleteService } from './athlete.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Athlete } from '../models/athlete.model';
import { Router } from '@angular/router';

describe('AthleteService', () => {
  let service: AthleteService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);
    routerSpyObj.url = '/fake-current-url';
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpyObj }]
    });
    service = TestBed.inject(AthleteService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all athletes', () => {
    const dummyResponse = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    };
    service.getAll(0, 10, 'lastName').subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r => r.method === 'GET' && r.url === '/api/athletes');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sortBy')).toBe('lastName');
    req.flush(dummyResponse);
  });

  it('should fetch filtered athletes', () => {
    const filters = { firstName: 'John', lastName: 'Doe' };
    const dummyResponse = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    };

    service.getFiltered(filters, 0, 10).subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r =>
      r.method === 'GET' &&
      r.url === '/api/athletes/filter' &&
      r.params.has('firstName') &&
      r.params.get('firstName') === 'John' &&
      r.params.has('lastName') &&
      r.params.get('lastName') === 'Doe'
    );
    req.flush(dummyResponse);
  });

  it('should fetch athlete by ID', () => {
    const dummyAthlete: Athlete = { licenseNumber: 'A001', firstName: 'John', lastName: 'Doe' } as Athlete;

    service.getById('A001').subscribe(response => {
      expect(response).toEqual(dummyAthlete);
    });

    const req = httpMock.expectOne('/api/athletes/A001');
    expect(req.request.method).toBe('GET');
    req.flush(dummyAthlete);
  });

  it('should create a new athlete', () => {
    const newAthlete: Athlete = { licenseNumber: 'A002', firstName: 'Jane', lastName: 'Doe' } as Athlete;

    service.create(newAthlete).subscribe(response => {
      expect(response).toEqual(newAthlete);
    });

    const req = httpMock.expectOne('/api/athletes');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newAthlete);
    req.flush(newAthlete);
  });

  it('should update an athlete', () => {
    const updatedAthlete: Athlete = { licenseNumber: 'A003', firstName: 'Jim', lastName: 'Beam' } as Athlete;

    service.update('A003', updatedAthlete).subscribe(response => {
      expect(response).toEqual(updatedAthlete);
    });

    const req = httpMock.expectOne('/api/athletes/A003');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedAthlete);
    req.flush(updatedAthlete);
  });

  it('should delete an athlete', () => {
    service.delete('A004').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('/api/athletes/A004');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should handle 401/403 errors and redirect to login', () => {
    service.getById('A005').subscribe({
      next: () => fail('should have failed with 401 error'),
      error: error => {
        expect(error.status).toBe(401);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
          queryParams: { returnUrl: '/fake-current-url' }
        });
      }
    });

    const req = httpMock.expectOne('/api/athletes/A005');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
