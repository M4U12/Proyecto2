import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('token');
  const rolUsuario = localStorage.getItem('rol');

  // verificar si hay sesión activa
  if (!token) {
    alert('Por favor, inicia sesión primero.');
    router.navigate(['/login']);
    return false;
  }

  // verificar el rol solo si la ruta exige un rol específico
  const rolRequerido = route.data['role'];
  const rolesRequeridos = route.data['roles'];

  //para multiples roles
  if (rolesRequeridos && Array.isArray(rolesRequeridos)) {
    if (rolesRequeridos.includes(rolUsuario)) {
      return true;
    } else {
      alert('Acceso Denegado: No tienes permisos para este módulo.');
      router.navigate(['/dashboard']); 
      return false;
    }
  }

  //para rol solitario
  if (rolRequerido) {
    if (rolUsuario === rolRequerido) {
      return true;
    } else {
      alert('Acceso Denegado: No tienes permisos para este módulo.');
      router.navigate(['/dashboard']); 
      return false;
    }
  }

  // si la ruta no exige rol específico, pero esta logueado, lo deja pasar
  return true; 
};