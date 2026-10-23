import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CatalogoLibros } from './catalogo-libros';

describe('CatalogoLibros', () => {
  let component: CatalogoLibros;
  let fixture: ComponentFixture<CatalogoLibros>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoLibros],
    }).compileComponents();

    fixture = TestBed.createComponent(CatalogoLibros);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
