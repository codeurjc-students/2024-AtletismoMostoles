import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssociationAtlComponent } from './association-atl.component';

describe('AssociationAtlComponent', () => {
  let component: AssociationAtlComponent;
  let fixture: ComponentFixture<AssociationAtlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssociationAtlComponent]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssociationAtlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct number of members', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const rows = compiled.querySelectorAll('.members-table tbody tr');
    expect(rows.length).toBe(component.members.length);
  });
});
