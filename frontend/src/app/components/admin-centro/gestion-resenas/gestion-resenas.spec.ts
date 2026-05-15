import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionResenas } from './gestion-resenas';

describe('GestionResenas', () => {
  let component: GestionResenas;
  let fixture: ComponentFixture<GestionResenas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionResenas]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionResenas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
