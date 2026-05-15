import { TestBed } from '@angular/core/testing';

import { TipoPistaService } from './tipo-pista-service';

describe('TipoPistaService', () => {
  let service: TipoPistaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoPistaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
