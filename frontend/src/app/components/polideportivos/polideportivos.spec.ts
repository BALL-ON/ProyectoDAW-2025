import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Polideportivos } from './polideportivos';

describe('Polideportivos', () => {
  let component: Polideportivos;
  let fixture: ComponentFixture<Polideportivos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Polideportivos]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Polideportivos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
