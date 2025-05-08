import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EquipmentService } from './equipment.service';
import { Equipment } from '../models/equipment.model';
import { Router } from '@angular/router';

describe('EquipmentService', () => {
  let service: EquipmentService;
  let httpMock: HttpTestingController;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const routerSpyObj = jasmine.createSpyObj('Router', ['navigate']);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: Router, useValue: routerSpyObj }]
    });
    service = TestBed.inject(EquipmentService);
    httpMock = TestBed.inject(HttpTestingController);
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all equipment', () => {
    const dummyResponse = { content: [], totalElements: 0, totalPages: 0, size: 10, number: 0 };

    service.getAll(0, 10, 'name').subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r => r.method === 'GET' && r.url === '/api/equipment');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sortBy')).toBe('name');
    req.flush(dummyResponse);
  });

  it('should fetch equipment by ID', () => {
    const dummyEquipment: Equipment = { id: 1, name: 'Hammer', description: 'Heavy hammer' } as Equipment;

    service.getById(1).subscribe(response => {
      expect(response).toEqual(dummyEquipment);
    });

    const req = httpMock.expectOne('/api/equipment/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyEquipment);
  });

  it('should create new equipment', () => {
    const newEquipment: Equipment = { id: 2, name: 'Discus', description: 'Steel discus' } as Equipment;

    service.create(newEquipment).subscribe(response => {
      expect(response).toEqual(newEquipment);
    });

    const req = httpMock.expectOne('/api/equipment');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newEquipment);
    req.flush(newEquipment);
  });

  it('should update equipment', () => {
    const updatedEquipment: Equipment = { id: 3, name: 'Javelin', description: 'Updated javelin' } as Equipment;

    service.update(3, updatedEquipment).subscribe(response => {
      expect(response).toEqual(updatedEquipment);
    });

    const req = httpMock.expectOne('/api/equipment/3');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedEquipment);
    req.flush(updatedEquipment);
  });

  it('should delete equipment', () => {
    service.delete(4).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('/api/equipment/4');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should handle 401/403 errors and redirect to login', () => {
    service.getById(5).subscribe({
      next: () => fail('should have failed with 401 error'),
      error: error => {
        expect(error.status).toBe(401);
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login'], {
          queryParams: { returnUrl: routerSpy.url }
        });
      }
    });

    const req = httpMock.expectOne('/api/equipment/5');
    req.flush({}, { status: 401, statusText: 'Unauthorized' });
  });
});
