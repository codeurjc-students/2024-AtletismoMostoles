import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  standalone: true,
  imports: [
    RouterLink,
    RouterOutlet
  ],
  styleUrls: ['./index.component.css']
})
export class IndexComponent {
  toggleMenu() {
    const menu = document.getElementById('dropdown-menu');
    if (menu) {
      menu.style.display = menu.style.display === 'flex' ? 'none' : 'flex';
    }
  }
}
