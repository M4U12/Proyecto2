import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Ciclos } from './ciclos';

describe('Ciclos', () => {
  let component: Ciclos;
  let fixture: ComponentFixture<Ciclos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Ciclos],
    }).compileComponents();

    fixture = TestBed.createComponent(Ciclos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
