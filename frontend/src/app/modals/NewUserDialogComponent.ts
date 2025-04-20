import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-new-user-dialog',
  standalone: true,
  template: `
    <h2 mat-dialog-title>Registrar Usuario</h2>
    <mat-dialog-content [formGroup]="form">
      <mat-form-field appearance="fill" style="width: 100%;">
        <mat-label>Nombre de usuario</mat-label>
        <input matInput formControlName="name">
        <mat-error *ngIf="form.get('name')?.invalid && form.get('name')?.touched">Obligatorio</mat-error>
      </mat-form-field>

      <mat-form-field appearance="fill" style="width: 100%;">
        <mat-label>Contraseña</mat-label>
        <input matInput type="password" formControlName="rawPassword">
        <mat-error *ngIf="form.get('rawPassword')?.invalid && form.get('rawPassword')?.touched">Obligatorio</mat-error>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="primary" (click)="onSave()">Registrar</button>
    </mat-dialog-actions>
  `,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class NewUserDialogComponent {
  form: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<NewUserDialogComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      rawPassword: ['', Validators.required]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.form.valid) {
      const userPayload = {
        name: this.form.value.name,
        rawPassword: this.form.value.rawPassword
      };
      this.dialogRef.close(userPayload);
    } else {
      this.form.markAllAsTouched();
    }
  }
}
