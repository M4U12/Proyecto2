import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GestionLibros } from './gestion-libros';

describe('GestionLibros', () => {
  let component: GestionLibros;
  let fixture: ComponentFixture<GestionLibros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionLibros],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionLibros);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
