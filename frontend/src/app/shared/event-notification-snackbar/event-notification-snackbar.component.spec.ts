import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventNotificationSnackbarComponent } from './event-notification-snackbar.component';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

describe('EventNotificationSnackbarComponent', () => {
  let component: EventNotificationSnackbarComponent;
  let fixture: ComponentFixture<EventNotificationSnackbarComponent>;
  let snackBarRefSpy: jasmine.SpyObj<MatSnackBarRef<EventNotificationSnackbarComponent>>;

  beforeEach(async () => {
    snackBarRefSpy = jasmine.createSpyObj('MatSnackBarRef', ['dismissWithAction']);

    await TestBed.configureTestingModule({
      imports: [EventNotificationSnackbarComponent],
      providers: [
        { provide: MAT_SNACK_BAR_DATA, useValue: { eventId: 42 } },
        { provide: MatSnackBarRef, useValue: snackBarRefSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventNotificationSnackbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crearse', () => {
    expect(component).toBeTruthy();
  });

  it('debe cerrar el snackbar al ejecutar verEvento', () => {
    component.getEvent();
    expect(snackBarRefSpy.dismissWithAction).toHaveBeenCalled();
  });
});
