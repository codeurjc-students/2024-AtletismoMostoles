import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { NewUserDialogComponent } from '../../modals/NewUserDialogComponent';
import { NgIf } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: true,
  imports: [
    NgIf,
    MatIconModule,
    MatMenuModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    RouterOutlet,
    RouterLink
  ]
})
export class UserProfileComponent implements OnInit {
  user: any;
  isLoggedIn = false;
  isAdmin = false;

  constructor(
    private authService: AuthService,
    private dialog: MatDialog,
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.authService.user.subscribe(user => {
      this.user = user;
      this.isLoggedIn = this.authService.isAuthenticated();
      this.isAdmin = this.authService.isAdmin();
    });
  }

  logout() {
    this.authService.logout();
  }

  login() {
    this.router.navigate(['/login']);
  }

  openNewUserDialog(): void {
    this.dialog.open(NewUserDialogComponent, {
      width: '400px'
    }).afterClosed().subscribe(result => {
      if (result) {
        this.userService.registerUser(result).subscribe(
          () => alert('✅ Usuario creado correctamente'),
          () => alert('❌ Error al crear el usuario')
        );
      }
    });
  }
}
