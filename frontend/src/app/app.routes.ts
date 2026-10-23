import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login'; 
import { Dashboard } from './components/dashboard/dashboard'; 
import { Ciclos } from './components/ciclos/ciclos'; 
import { Empleados } from './components/empleados/empleados'; 
import { roleGuard } from './guards/auth.guard';
import { Carreras } from './components/carreras/carreras';
import { Niveles } from './components/niveles/niveles';
import { Grados } from './components/grados/grados';
import { Cursos } from './components/cursos/cursos';
import { Curriculos } from './components/curriculos/curriculos';
import { Perfil } from './components/perfil/perfil';
import { Inicio } from './components/inicio/inicio';
import { Estudiantes } from './components/estudiantes/estudiantes';
import { Inscripciones } from './components/inscripciones/inscripciones';
import { Asignaciones } from './components/asignaciones/asignaciones';
import { ControlMaestro } from './components/control-maestro/control-maestro';
import { Asistencias } from './components/asistencias/asistencias';
import { Notas } from './components/notas/notas';
import { CursosEstudiante } from './components/cursos-estudiante/cursos-estudiante';
import { Boleta } from './components/boleta/boleta';
import { AdminEstudiantes } from './components/admin-estudiantes/admin-estudiantes';
import { GestionLibros } from './components/gestion-libros/gestion-libros';
import { GestionPrestamos } from './components/gestion-prestamos/gestion-prestamos';
import { CatalogoLibros } from './components/catalogo-libros/catalogo-libros';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: Dashboard,
    children: [
      { path: '', component: Inicio },
      { 
        path: 'ciclos', 
        component: Ciclos,
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN' } 
      },
      { 
        path: 'empleados', 
        component: Empleados,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR' } 
      },
      { 
        path: 'administradores', 
        component: Empleados,    
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN' } 
      },
      { 
        path: 'carreras', 
        component: Carreras,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR' } 
      },
      {
        path: 'niveles',
        component: Niveles,
        canActivate: [roleGuard],
        data: { role: 'SUPER_ADMIN'}
      },
      {
        path: 'grados',
        component: Grados,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR'}
      },
      {
        path: 'cursos',
        component: Cursos,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR'}
      },
      {
        path: 'curriculos',
        component: Curriculos,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR'}
      },
      { 
        path: 'perfil', 
        component: Perfil,
        canActivate: [roleGuard] 
      },
      {
        path: 'estudiantes',
        component: Estudiantes,
        canActivate: [roleGuard],
        data: { role: 'SECRETARIA'}
      },
      {
        path: 'inscripciones',
        component: Inscripciones,
        canActivate: [roleGuard],
        data: { role: 'SECRETARIA'}
      },
      {
        path: 'asignaciones',
        component: Asignaciones,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR'},
      },
      { 
        path: 'control-maestro', 
        component: ControlMaestro,
        canActivate: [roleGuard],
        data: { role: 'MAESTRO' }
      },
      { 
        path: 'asistencia', 
        component: Asistencias,
        canActivate: [roleGuard],
        data: { role: 'MAESTRO' }
      },
      { 
        path: 'notas', 
        component: Notas,
        canActivate: [roleGuard],
        data: { role: 'MAESTRO' }
      },
      { 
        path: 'cursos-estudiante', 
        component: CursosEstudiante,
        canActivate: [roleGuard],
        data: { role: 'ESTUDIANTE' }
      },
      { 
        path: 'boleta', 
        component: Boleta,
        canActivate: [roleGuard],
        data: { role: 'ESTUDIANTE' }
      },
      { 
        path: 'admin-estudiantes', 
        component: AdminEstudiantes,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR' } 
      },
      { 
        path: 'gestion-libros', 
        component: GestionLibros,
        canActivate: [roleGuard],
        data: { role: 'BIBLIOTECARIO' } 
      },
      { 
        path: 'gestion-prestamos', 
        component: GestionPrestamos,
        canActivate: [roleGuard],
        data: { role: 'BIBLIOTECARIO' } 
      },
      { 
        path: 'catalogo-libros', 
        component: CatalogoLibros,
        canActivate: [roleGuard],
        data: { roles: ['ESTUDIANTE', 'MAESTRO'] } 
      },
    ]
  },
  { path: '**', redirectTo: '' }
];