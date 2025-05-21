import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-new-discipline-dialog',
  template: `
    <h2 mat-dialog-title>Nueva Disciplina</h2>
    <mat-dialog-content>
      <form [formGroup]="data.disciplineForm">
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Nombre de la Disciplina</mat-label>
          <input matInput formControlName="name" >
        </mat-form-field>
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Horarios de Entrenamiento</mat-label>
          <input matInput formControlName="description">
        </mat-form-field>
        <mat-form-field appearance="fill" style="width: 100%;">
          <mat-label>Enlace de Imagen</mat-label>
          <input matInput formControlName="imageLink">
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="primary" (click)="onSave()" [disabled]="data.disciplineForm.invalid">Guardar</button>
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
    MatButtonModule
  ]
})
export class NewDisciplineDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<NewDisciplineDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { disciplineForm: FormGroup }
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.data.disciplineForm.valid) {
      this.dialogRef.close('save');
    }
  }
}
