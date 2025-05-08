import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ResultService } from './result.service';
import { Router } from '@angular/router';
import { Results } from '../models/results.model';

describe('ResultService', () => {
  let service: ResultService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate'], { url: '/current-url' });

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpyObj }]
    });

    service = TestBed.inject(ResultService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all results', () => {
    const dummyResponse = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    };

    service.getAll(0, 10, 'date', 1, 2).subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r =>
      r.method === 'GET' &&
      r.url === '/api/results' &&
      r.params.get('page') === '0' &&
      r.params.get('size') === '10' &&
      r.params.get('sortBy') === 'date' &&
      r.params.get('eventId') === '1' &&
      r.params.get('disciplineId') === '2'
    );
    req.flush(dummyResponse);
  });

  it('should fetch result by ID', () => {
    const dummyResult: Results = { id: 1, value: 12.5 } as Results;

    service.getById(1).subscribe(response => {
      expect(response).toEqual(dummyResult);
    });

    const req = httpMock.expectOne('/api/results/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyResult);
  });

  it('should create a new result', () => {
    const newResult: Results = { id: 1, value: 13.5 } as Results;

    service.create(newResult).subscribe(response => {
      expect(response).toEqual(newResult);
    });

    const req = httpMock.expectOne('/api/results');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newResult);
    req.flush(newResult);
  });

  it('should update a result', () => {
    const updatedResult: Results = { id: 1, value: 14.0 } as Results;

    service.update(1, updatedResult).subscribe(response => {
      expect(response).toEqual(updatedResult);
    });

    const req = httpMock.expectOne('/api/results/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedResult);
    req.flush(updatedResult);
  });

  it('should delete a result', () => {
    service.delete(1).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('/api/results/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should create multiple results', () => {
    const newResults: Results[] = [
      { id: 1, value: 15.0 } as Results,
      { id: 2, value: 16.0 } as Results
    ];

    service.createMultiple(newResults).subscribe(response => {
      expect(response).toEqual(newResults);
    });

    const req = httpMock.expectOne('/api/results/batch');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newResults);
    req.flush(newResults);
  });

  it('should handle 401/403 errors and redirect to login', () => {
    service.getById(1).subscribe({
      next: () => fail('should have failed with 401 error'),
      error: error => {
        expect(error.status).toBe(401);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
          queryParams: { returnUrl: '/current-url' }
        });
      }
    });

    const req = httpMock.expectOne('/api/results/1');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
