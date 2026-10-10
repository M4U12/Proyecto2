import { TestBed } from '@angular/core/testing';
import { Ciclo } from './ciclo';

describe('Ciclo', () => {
  let service: Ciclo;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Ciclo);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
