import { TestBed } from '@angular/core/testing';

import { MensajeContacto } from './mensaje-contacto';

describe('MensajeContacto', () => {
  let service: MensajeContacto;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MensajeContacto);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
