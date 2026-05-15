import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionPistas } from './gestion-pistas';

describe('GestionPistas', () => {
  let component: GestionPistas;
  let fixture: ComponentFixture<GestionPistas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionPistas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionPistas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
