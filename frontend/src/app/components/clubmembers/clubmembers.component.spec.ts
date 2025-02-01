import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClubMembersComponent } from './clubmembers.component';

describe('ClubmembersComponent', () => {
  let component: ClubMembersComponent;
  let fixture: ComponentFixture<ClubMembersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClubMembersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClubMembersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
