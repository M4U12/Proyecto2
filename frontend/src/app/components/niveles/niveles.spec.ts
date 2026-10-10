import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Niveles } from './niveles';

describe('Niveles', () => {
  let component: Niveles;
  let fixture: ComponentFixture<Niveles>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Niveles],
    }).compileComponents();

    fixture = TestBed.createComponent(Niveles);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
