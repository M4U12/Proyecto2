import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Tesoreria } from './tesoreria';

describe('Tesoreria', () => {
  let component: Tesoreria;
  let fixture: ComponentFixture<Tesoreria>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Tesoreria],
    }).compileComponents();

    fixture = TestBed.createComponent(Tesoreria);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
