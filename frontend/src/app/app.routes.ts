import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login'; 
import { Dashboard } from './components/dashboard/dashboard'; 
import { Ciclos } from './components/ciclos/ciclos'; 
import { Empleados } from './components/empleados/empleados'; 
import { roleGuard } from './services/auth.guard';
import { Carreras } from './components/carreras/carreras';
import { Niveles } from './components/niveles/niveles';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: Dashboard,
    children: [
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
      }
    ]
  }
];