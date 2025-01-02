import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClubmembersComponent } from './clubmembers.component';

describe('ClubmembersComponent', () => {
  let component: ClubmembersComponent;
  let fixture: ComponentFixture<ClubmembersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClubmembersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClubmembersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
