import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CursosEstudiante } from './cursos-estudiante';

describe('CursosEstudiante', () => {
  let component: CursosEstudiante;
  let fixture: ComponentFixture<CursosEstudiante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CursosEstudiante],
    }).compileComponents();

    fixture = TestBed.createComponent(CursosEstudiante);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
