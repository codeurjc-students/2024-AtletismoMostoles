import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserProfileComponent } from './user-profile.component';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('UserProfileComponent', () => {
  let component: UserProfileComponent;
  let fixture: ComponentFixture<UserProfileComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout', 'isAuthenticated', 'isAdmin'], { user: of({ id: 1, name: 'TestUser' }) });
    userServiceSpy = jasmine.createSpyObj('UserService', ['getUsersAdmin', 'registerUser', 'deleteUser']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [UserProfileComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: UserService, useValue: userServiceSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserProfileComponent);
    component = fixture.componentInstance;
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit - carga usuarios si es admin', () => {
    authServiceSpy.isAdmin.and.returnValue(true);
    userServiceSpy.getUsersAdmin.and.returnValue(of({ content: [{ id: 1, name: 'User1' }] }));

    component.ngOnInit();

    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(authServiceSpy.isAdmin).toHaveBeenCalled();
    expect(userServiceSpy.getUsersAdmin).toHaveBeenCalled();
  });

  it('ngOnInit - no carga usuarios si no es admin', () => {
    authServiceSpy.isAdmin.and.returnValue(false);

    component.ngOnInit();

    expect(authServiceSpy.isAuthenticated).toHaveBeenCalled();
    expect(authServiceSpy.isAdmin).toHaveBeenCalled();
    expect(userServiceSpy.getUsersAdmin).not.toHaveBeenCalled();
  });

  it('loadUsers - carga usuarios correctamente', () => {
    const mockUsers = { content: [{ id: 1, name: 'User1' }] };
    userServiceSpy.getUsersAdmin.and.returnValue(of(mockUsers));

    component.loadUsers();

    expect(component.users).toEqual(mockUsers.content);
  });

  it('openNewUserDialog - crea usuario exitosamente', fakeAsync(() => {
    const afterClosedSpy = jasmine.createSpyObj({ subscribe: (fn: (res: any) => void) => fn({ username: 'NewUser' }) });
    dialogSpy.open.and.returnValue({ afterClosed: () => afterClosedSpy } as any);

    userServiceSpy.registerUser.and.returnValue(of({}));
    spyOn(window, 'alert');

    userServiceSpy.getUsersAdmin.and.returnValue(of({ content: [] })); // para loadUsers

    component.openNewUserDialog();
    tick();

    expect(userServiceSpy.registerUser).toHaveBeenCalled();
    expect(userServiceSpy.getUsersAdmin).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('✅ Usuario creado correctamente');
  }));

  it('deleteSelectedUsers - elimina usuarios seleccionados', fakeAsync(() => {
    const mockUsers = [{ id: 1 }, { id: 2 }];
    component.selection.select(...mockUsers);
    userServiceSpy.deleteUser.and.returnValue(of(void 0));
    userServiceSpy.getUsersAdmin.and.returnValue(of({ content: [] }));

    spyOn(window, 'alert');

    component.deleteSelectedUsers();
    tick();

    expect(userServiceSpy.deleteUser).toHaveBeenCalledTimes(2);
    expect(userServiceSpy.getUsersAdmin).toHaveBeenCalled();
    expect(window.alert).toHaveBeenCalledWith('✅ Usuarios eliminados correctamente');
  }));

  it('logout debería llamar al servicio de logout', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });

  it('login debería navegar a /login', () => {
    component.login();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('masterToggle - selecciona todos cuando no están todos seleccionados', () => {
    component.users = [{ id: 1 }, { id: 2 }];
    component.selection.clear();
    component.masterToggle();
    expect(component.selection.selected.length).toBe(2);
  });

  it('masterToggle - limpia la selección si todos estaban seleccionados', () => {
    component.users = [{ id: 1 }, { id: 2 }];
    component.selection.select(...component.users);
    component.masterToggle();
    expect(component.selection.selected.length).toBe(0);
  });

  it('isAllSelected debería devolver true cuando todos están seleccionados', () => {
    component.users = [{ id: 1 }, { id: 2 }];
    component.selection.select(...component.users);
    expect(component.isAllSelected()).toBeTrue();
  });

  it('isAllSelected debería devolver false cuando no todos están seleccionados', () => {
    component.users = [{ id: 1 }, { id: 2 }];
    component.selection.select({ id: 1 });
    expect(component.isAllSelected()).toBeFalse();
  });
});
