import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MisPrestamos } from './mis-prestamos';

describe('MisPrestamos', () => {
  let component: MisPrestamos;
  let fixture: ComponentFixture<MisPrestamos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisPrestamos],
    }).compileComponents();

    fixture = TestBed.createComponent(MisPrestamos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
