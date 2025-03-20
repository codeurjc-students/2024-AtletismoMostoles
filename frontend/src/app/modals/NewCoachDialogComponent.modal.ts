import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { NgForOf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-new-coach-dialog',
  template: `
    <h2 mat-dialog-title>Nuevo Entrenador</h2>
    <mat-dialog-content>
      <form [formGroup]="data.coachForm">
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Número de Licencia</mat-label>
          <input matInput formControlName="licenseNumber">
        </mat-form-field>
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="firstName">
        </mat-form-field>
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Apellido</mat-label>
          <input matInput formControlName="lastName">
        </mat-form-field>
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Disciplinas</mat-label>
          <mat-select formControlName="disciplines" multiple>
            <mat-option *ngFor="let discipline of data.disciplines" [value]="discipline.id">
              {{ discipline.name }}
            </mat-option>
          </mat-select>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="primary" (click)="onSave()">Guardar</button>
    </mat-dialog-actions>
  `,
  standalone: true,
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    ReactiveFormsModule,
    NgForOf,
    MatButtonModule
  ]
})
export class NewCoachDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<NewCoachDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { coachForm: FormGroup; disciplines: any[] }
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    this.dialogRef.close('save');
  }
}
