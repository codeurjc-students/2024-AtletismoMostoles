import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisciplineDetailsComponent } from './discipline-details.component';

describe('DisciplineDetailsComponent', () => {
  let component: DisciplineDetailsComponent;
  let fixture: ComponentFixture<DisciplineDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisciplineDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisciplineDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
