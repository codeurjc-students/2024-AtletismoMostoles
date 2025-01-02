import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-association-atl',
  templateUrl: './association-atl.component.html',
  standalone: true,
  styleUrls: ['./association-atl.component.css']
})
export class AssociationAtlComponent implements OnInit {
  members: { name: string; role: string }[] = [
    { name: 'John Doe', role: 'Presidente' },
    { name: 'Jane Smith', role: 'Secretaria' },
    { name: 'Alice Brown', role: 'Tesorera' }
  ];

  constructor() {}

  ngOnInit(): void {
    // Lógica de inicialización
  }
}
