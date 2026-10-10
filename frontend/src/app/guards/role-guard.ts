import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Lee desde localStorage igual que en el dashboard
  const usuarioGuardado = localStorage.getItem('usuarioLogueado');
  let rolUsuario = '';
  
  if (usuarioGuardado) {
    const usuario = JSON.parse(usuarioGuardado);
    rolUsuario = usuario.rol;
  }
  
  const rolRequerido = route.data['role'];

  if (rolUsuario === rolRequerido) {
    return true; 
  }

  alert('Acceso Denegado: No tienes permisos para este módulo.');
  router.navigate(['/dashboard']);
  return false;
};