import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfiguracionService } from '../../services/configuracion.service';

@Component({
  selector: 'app-configuraciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './configuraciones.html'
})
export class Configuraciones implements OnInit {
  configuraciones: any[] = [];
  cargando: boolean = false;

  constructor(
    private configService: ConfiguracionService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit(): void {
    this.cargarConfiguraciones();
  }

  cargarConfiguraciones(): void {
    this.cargando = true;
    this.configService.listarConfiguraciones().subscribe({
      next: (datos) => {
        this.configuraciones = datos;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar configuraciones:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardarCambio(config: any): void {
    if (config.valor < 0) {
      alert('El valor no puede ser negativo.');
      return;
    }

    if (confirm(`¿Estás seguro de actualizar el parámetro a Q${config.valor}?`)) {
      this.configService.actualizarConfiguracion(config.clave, config.valor).subscribe({
        next: () => alert('Configuración actualizada exitosamente en el sistema.'),
        error: (err) => alert(err.error?.error || 'Error al actualizar.')
      });
    }
  }
}