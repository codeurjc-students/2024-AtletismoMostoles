import { TestBed } from '@angular/core/testing';
import { CoachService } from './coach.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Coach } from '../models/coach.model';
import { Router } from '@angular/router';
import { Page } from '../models/page.model';

describe('CoachService', () => {
  let service: CoachService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate'], { url: '/current-url' });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpyObj }]
    });
    service = TestBed.inject(CoachService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all coaches', () => {
    const dummyResponse: Page<Coach> = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    };

    service.getAll(0, 10, 'lastName').subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r => r.method === 'GET' && r.url === '/api/coaches');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sortBy')).toBe('lastName');
    req.flush(dummyResponse);
  });

  it('should fetch filtered coaches', () => {
    const filters = { firstName: 'Jane', lastName: 'Doe' };
    const dummyResponse: Page<Coach> = {
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
      r.url === '/api/coaches/filter' &&
      r.params.get('firstName') === 'Jane' &&
      r.params.get('lastName') === 'Doe'
    );
    req.flush(dummyResponse);
  });

  it('should fetch coach by ID', () => {
    const dummyCoach: Coach = { licenseNumber: 'C001', firstName: 'Jane', lastName: 'Doe' } as Coach;

    service.getById('C001').subscribe(response => {
      expect(response).toEqual(dummyCoach);
    });

    const req = httpMock.expectOne('/api/coaches/C001');
    expect(req.request.method).toBe('GET');
    req.flush(dummyCoach);
  });

  it('should create a new coach', () => {
    const newCoach: Coach = { licenseNumber: 'C002', firstName: 'John', lastName: 'Smith' } as Coach;

    service.create(newCoach).subscribe(response => {
      expect(response).toEqual(newCoach);
    });

    const req = httpMock.expectOne('/api/coaches');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newCoach);
    req.flush(newCoach);
  });

  it('should update a coach', () => {
    const updatedCoach: Coach = { licenseNumber: 'C003', firstName: 'Anna', lastName: 'Taylor' } as Coach;

    service.update('C003', updatedCoach).subscribe(response => {
      expect(response).toEqual(updatedCoach);
    });

    const req = httpMock.expectOne('/api/coaches/C003');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedCoach);
    req.flush(updatedCoach);
  });

  it('should delete a coach', () => {
    service.delete('C004').subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('/api/coaches/C004');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should handle 401/403 errors and redirect to login', () => {
    service.getById('C005').subscribe({
      next: () => fail('should have failed with 401 error'),
      error: error => {
        expect(error.status).toBe(401);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
          queryParams: { returnUrl: routerSpy.url }
        });
      }
    });

    const req = httpMock.expectOne('/api/coaches/C005');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
