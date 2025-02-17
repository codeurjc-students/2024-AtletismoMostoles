import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { NgIf } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  standalone: true,
  imports: [
    NgIf,
    RouterLink,
    RouterOutlet,
    HttpClientModule
  ],
  styleUrls: ['./index.component.css']
})
export class IndexComponent implements OnInit {
  isLoggedIn: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Suscribirse a los cambios del estado de autenticación
    this.authService.user.subscribe(user => {
      this.isLoggedIn = !!user; // Se asegura de que el usuario esté autenticado
      console.log("AuthService:", this.isLoggedIn);
    });
  }

  /**
   * Alterna la visibilidad del menú desplegable
   */
  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }

  /**
   * Cierra sesión del usuario
   */
  logout() {
    this.authService.logout();
  }

  /**
   * Redirige a la página de login
   */
  login() {
    this.router.navigate(['/login']);
  }
}
