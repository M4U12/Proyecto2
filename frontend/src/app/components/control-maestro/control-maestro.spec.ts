import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlMaestro } from './control-maestro';

describe('ControlMaestro', () => {
  let component: ControlMaestro;
  let fixture: ComponentFixture<ControlMaestro>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ControlMaestro],
    }).compileComponents();

    fixture = TestBed.createComponent(ControlMaestro);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
