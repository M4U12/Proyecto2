import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GestionPrestamos } from './gestion-prestamos';

describe('GestionPrestamos', () => {
  let component: GestionPrestamos;
  let fixture: ComponentFixture<GestionPrestamos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionPrestamos],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionPrestamos);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
