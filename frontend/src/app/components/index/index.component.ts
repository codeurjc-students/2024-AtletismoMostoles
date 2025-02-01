import { Component, OnInit } from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { NgIf } from '@angular/common';

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
export class IndexComponent implements OnInit{
  isLoogedIn: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoogedIn = this.authService.isAuthenticated();
    console.log("loged:", this.isLoogedIn);
  }

  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }

  logout() {
    if(!this.isLoogedIn){
      this.router.navigate(['/login']);
    }
    this.authService.logout();
  }

  login() {
    this.router.navigate(['/login']);
  }
}
