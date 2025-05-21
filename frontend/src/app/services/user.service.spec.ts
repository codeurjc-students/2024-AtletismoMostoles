import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a user', () => {
    const dummyUser = { name: 'test', password: '123456' };

    service.registerUser(dummyUser).subscribe(response => {
      expect(response).toEqual({ message: 'Registered' });
    });

    const req = httpMock.expectOne('/api/users/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dummyUser);
    req.flush({ message: 'Registered' });
  });

  it('should fetch all users', () => {
    const dummyUsers = [{ id: 1, name: 'User1' }, { id: 2, name: 'User2' }];

    service.getAll().subscribe(users => {
      expect(users.length).toBe(2);
      expect(users).toEqual(dummyUsers);
    });

    const req = httpMock.expectOne('/api/users');
    expect(req.request.method).toBe('GET');
    req.flush(dummyUsers);
  });

  it('should delete a user', () => {
    service.deleteUser(1).subscribe(response => {
      expect(response).toBeNull();
    });

    const req = httpMock.expectOne('/api/admin/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should update a user', () => {
    const updatedData = { name: 'Updated User' };

    service.updateUser(1, updatedData).subscribe(response => {
      expect(response).toEqual({ id: 1, ...updatedData });
    });

    const req = httpMock.expectOne('/api/admin/user/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedData);
    req.flush({ id: 1, ...updatedData });
  });

  it('should get users for admin', () => {
    const adminUsers = [{ id: 1, name: 'AdminUser1' }];

    service.getUsersAdmin().subscribe(users => {
      expect(users).toEqual(adminUsers);
    });

    const req = httpMock.expectOne('/api/admin/users');
    expect(req.request.method).toBe('GET');
    req.flush(adminUsers);
  });
});
