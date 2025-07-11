import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-event-notification-snackbar',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './event-notification-snackbar.component.html',
  styleUrls: ['./event-notification-snackbar.component.css']
})
export class EventNotificationSnackbarComponent {
  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: any,
    private snackBarRef: MatSnackBarRef<EventNotificationSnackbarComponent>
  ) {}

  verEvento(): void {
    this.snackBarRef.dismissWithAction();
  }
}
