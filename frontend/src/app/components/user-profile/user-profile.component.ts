import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService, NotificacionData } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { NewUserDialogComponent } from '../../modals/NewUserDialogComponent';
import { NgIf, NgForOf } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css'],
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    MatIconModule,
    MatMenuModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    RouterOutlet,
    RouterLink,
    MatTableModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSortModule
  ]
})
export class UserProfileComponent implements OnInit {
  user: any;
  isLoggedIn = false;
  isAdmin = false;
  users: any[] = [];
  selection = new SelectionModel<any>(true, []);
  notificacionesPendientes: NotificacionData[] = [];

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

      this.notificacionesPendientes = this.authService.notificacionesPendientes;

      if (this.isAdmin) {
        this.loadUsers();
      }
    });
  }

  verDetalles(eventoId: number): void {
    this.router.navigate([`/eventos/${eventoId}`]);
  }

  loadUsers(): void {
    this.userService.getUsersAdmin().subscribe(users => {
      this.users = users.content;
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
          () => {
            this.loadUsers();
            alert('✅ Usuario creado correctamente');
          },
          () => alert('❌ Error al crear el usuario')
        );
      }
    });
  }

  deleteSelectedUsers(): void {
    const promises = this.selection.selected.map(u => this.userService.deleteUser(u.id).toPromise());
    Promise.all(promises).then(() => {
      this.loadUsers();
      this.selection.clear();
      alert('✅ Usuarios eliminados correctamente');
    }).catch(() => {
      alert('❌ Error al eliminar usuarios');
    });
  }

  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.users.forEach(user => this.selection.select(user));
    }
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.users.length;
    return numSelected === numRows;
  }
}
