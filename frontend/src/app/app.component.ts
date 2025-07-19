import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';
import { WebSocketService } from './services/web-socket.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EventNotificationSnackbarComponent } from './shared/event-notification-snackbar/event-notification-snackbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatSnackBarModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'frontend';

  constructor(
    public authService: AuthService,
    private webSocketService: WebSocketService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    (window as any).authService = this.authService;
  }

  ngOnInit(): void {
    this.webSocketService.escucharEventos((evento) => {
      const ref = this.snackBar.openFromComponent(EventNotificationSnackbarComponent, {
        data: evento,
        duration: 8000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['custom-snackbar-container']
      });

      ref.onAction().subscribe(() => {
        this.router.navigate([`/eventos/${evento.eventoId}`]);
      });
    });
  }
}
