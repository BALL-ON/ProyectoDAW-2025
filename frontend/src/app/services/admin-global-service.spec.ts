import { TestBed } from '@angular/core/testing';

import { AdminGlobalService } from './admin-global-service';

describe('AdminGlobalService', () => {
  let service: AdminGlobalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminGlobalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
