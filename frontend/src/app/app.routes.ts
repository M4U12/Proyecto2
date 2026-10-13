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
        data: { role: 'ADMINISTRADOR'}
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
      
    ]
  },
  { path: '**', redirectTo: '' }
];