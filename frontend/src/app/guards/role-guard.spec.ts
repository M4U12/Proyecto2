import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Obtencion del rol guardado en la sesión
  const rolUsuario = sessionStorage.getItem('rol'); 
  // Se obtiene que rol exige la ruta que intentan visitar
  const rolRequerido = route.data['role'];

  if (rolUsuario === rolRequerido) {
    return true; 
  }

  // si no tiene permisos, se envía de vuelta al inicio del panel
  alert('Acceso Denegado: No tienes permisos para este módulo.');
  router.navigate(['/dashboard']);
  return false;
};