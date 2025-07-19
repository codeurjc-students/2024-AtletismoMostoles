import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonModule, NgForOf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-add-results-dialog',
  template: `
    <h2 mat-dialog-title>Agregar Resultados</h2>
    <mat-dialog-content>
      <form [formGroup]="resultsForm">
        <div formArrayName="results">
          <div *ngFor="let group of resultControls.controls; let i = index" [formGroupName]="i">
            <mat-form-field appearance="fill" style="width: 100%;">
              <mat-label>Atleta</mat-label>
              <mat-select formControlName="athleteLicenseNumber">
                <mat-option *ngFor="let athlete of data.athletes" [value]="athlete.licenseNumber">
                  {{ athlete.firstName }} {{ athlete.lastName }}
                </mat-option>
              </mat-select>
              <mat-error *ngIf="group.get('athleteLicenseNumber')?.invalid && group.get('athleteLicenseNumber')?.touched">
                El atleta es obligatorio.
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="fill" style="width: 100%;">
              <mat-label>Disciplina</mat-label>
              <mat-select formControlName="disciplineId">
                <mat-option *ngFor="let discipline of data.disciplines" [value]="discipline.id">
                  {{ discipline.name }}
                </mat-option>
              </mat-select>
              <mat-error *ngIf="group.get('disciplineId')?.invalid && group.get('disciplineId')?.touched">
                La disciplina es obligatoria.
              </mat-error>
            </mat-form-field>

            <mat-form-field appearance="fill" style="width: 100%;">
              <mat-label>Resultado</mat-label>
              <input matInput formControlName="value" type="text">
              <mat-error *ngIf="group.get('value')?.invalid && group.get('value')?.touched">
                El resultado es obligatorio.
              </mat-error>
            </mat-form-field>

            <button mat-button color="warn" *ngIf="resultControls.length > 1" (click)="removeResult(i)">
              Eliminar
            </button>
            <hr />
          </div>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="addResult()">Añadir otro</button>
      <button mat-button (click)="onCancel()">Cancelar</button>
      <button mat-raised-button color="primary" (click)="onSave()">Guardar</button>
    </mat-dialog-actions>
  `,
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDialogModule,
    NgForOf,
    MatButtonModule
  ]
})
export class AddResultsDialogComponent {
  resultsForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<AddResultsDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { eventId: number; disciplines: any[]; athletes: any[] },
    private fb: FormBuilder
  ) {
    this.resultsForm = this.fb.group({
      results: this.fb.array([this.createResultGroup()])
    });
  }

  get resultControls(): FormArray {
    return this.resultsForm.get('results') as FormArray;
  }

  createResultGroup(): FormGroup {
    return this.fb.group({
      athleteLicenseNumber: ['', Validators.required],
      disciplineId: ['', Validators.required],
      value: ['', Validators.required]
    });
  }

  addResult(): void {
    this.resultControls.push(this.createResultGroup());
  }

  removeResult(index: number): void {
    this.resultControls.removeAt(index);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.resultsForm.valid) {
      const payload = this.resultControls.value.map((res: any) => ({
        atletaId: res.athleteLicenseNumber,
        eventoId: this.data.eventId,
        disciplinaId: res.disciplineId,
        valor: res.value
      }));

      this.dialogRef.close(payload);
    } else {
      this.resultsForm.markAllAsTouched();
    }
  }

}
