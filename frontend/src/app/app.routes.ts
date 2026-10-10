import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login'; 
import { Dashboard } from './components/dashboard/dashboard'; 
import { Ciclos } from './components/ciclos/ciclos'; 
import { Empleados } from './components/empleados/empleados'; // <-- Añadido
import { roleGuard } from './services/auth.guard';

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
        data: { role: 'ADMINISTRADOR' } 
      },
      { 
        path: 'empleados', 
        component: Empleados,
        canActivate: [roleGuard],
        data: { role: 'ADMINISTRADOR' } 
      }
    ]
  }
];