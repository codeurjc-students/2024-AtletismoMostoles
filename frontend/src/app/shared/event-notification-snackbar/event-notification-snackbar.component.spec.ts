import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventNotificationSnackbarComponent } from './event-notification-snackbar.component';

describe('EventNotificationSnackbarComponent', () => {
  let component: EventNotificationSnackbarComponent;
  let fixture: ComponentFixture<EventNotificationSnackbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventNotificationSnackbarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EventNotificationSnackbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
