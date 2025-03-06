import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-equipment-dialog',
  template: `
    <h2 mat-dialog-title>Agregar Equipamiento</h2>
    <mat-dialog-content>
      <form [formGroup]="equipmentForm">
        <mat-form-field appearance="fill" floatLabel="always" style="width: 100%;">
          <mat-label>Nombre del Equipamiento</mat-label>
          <input matInput formControlName="name" required>
        </mat-form-field>
      </form>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="primary" (click)="onSave()" [disabled]="equipmentForm.invalid">Guardar</button>
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
export class EquipmentDialogComponent {
  equipmentForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<EquipmentDialogComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: { equipment: any[] }
  ) {
    this.equipmentForm = this.fb.group({
      name: ['', Validators.required]
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.equipmentForm.valid) {
      this.dialogRef.close(this.equipmentForm.value);
    }
  }
}
