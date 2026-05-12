import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MensajesContacto } from './mensajes-contacto';

describe('MensajesContacto', () => {
  let component: MensajesContacto;
  let fixture: ComponentFixture<MensajesContacto>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MensajesContacto]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MensajesContacto);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
