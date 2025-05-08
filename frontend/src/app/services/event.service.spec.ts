import { TestBed } from '@angular/core/testing';
import { EventService } from './event.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Event } from '../models/event.model';
import { EventCreate } from '../models/event-create.model';

describe('EventService', () => {
  let service: EventService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(EventService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all events', () => {
    const dummyResponse = {
      content: [],
      totalElements: 0,
      totalPages: 0,
      size: 10,
      number: 0
    };

    service.getAll(0, 10, 'date').subscribe(response => {
      expect(response).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(r => r.method === 'GET' && r.url === '/api/events');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    expect(req.request.params.get('sortBy')).toBe('date');
    req.flush(dummyResponse);
  });

  it('should fetch event by ID', () => {
    const dummyEvent: Event = {
      id: 1,
      name: 'Sample Event',
      date: '2025-10-10',
      organizedByClub: true,
      mapLink: 'maplink.com',
      imageLink: 'image.jpg',
      disciplines: [{
        id: 1,
        name: '100m Sprint',
        description: 'Sprint discipline'
      }],
      results: []
    };

    service.getById(1).subscribe(response => {
      expect(response).toEqual(dummyEvent);
    });

    const req = httpMock.expectOne('/api/events/1');
    expect(req.request.method).toBe('GET');
    req.flush(dummyEvent);
  });

  it('should create a new event', () => {
    const newEventCreate: EventCreate = {
      name: 'New Event',
      date: '2025-10-15',
      imageUrl: 'newimage.jpg',
      mapUrl: 'newmaplink.com',
      isOrganizedByClub: true,
      disciplines: [{ id: 1 }]
    };

    const createdEvent: Event = {
      id: 2,
      name: 'New Event',
      date: '2025-10-15',
      organizedByClub: true,
      mapLink: 'newmaplink.com',
      imageLink: 'newimage.jpg',
      disciplines: [{
        id: 1,
        name: '100m Sprint',
        description: 'Sprint discipline'
      }],
      results: []
    };

    service.create(newEventCreate).subscribe(response => {
      expect(response).toEqual(createdEvent);
    });

    const req = httpMock.expectOne('/api/events');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newEventCreate);
    req.flush(createdEvent);
  });

  it('should update an event', () => {
    const updatedEvent: Event = {
      id: 2,
      name: 'Updated Event',
      date: '2025-11-01',
      organizedByClub: false,
      mapLink: 'updatedmaplink.com',
      imageLink: 'updatedimage.jpg',
      disciplines: [{
        id: 1,
        name: '100m Sprint',
        description: 'Sprint discipline'
      }],
      results: []
    };

    service.update(2, updatedEvent).subscribe(response => {
      expect(response).toEqual(updatedEvent);
    });

    const req = httpMock.expectOne('/api/events/2');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedEvent);
    req.flush(updatedEvent);
  });

  it('should delete an event', () => {
    service.delete(2).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('/api/events/2');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
