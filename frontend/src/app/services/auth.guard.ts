import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const roleGuard: CanActivateFn = (route, state) => {
  // Inyectamos el enrutador por si necesitamos patear al intruso de vuelta al login
  const router = inject(Router);
  
  // Busca la sesion
  const usuarioGuardado = localStorage.getItem('usuarioLogueado');

  if (usuarioGuardado) {
    const usuario = JSON.parse(usuarioGuardado);
    
    // rol que exige la ruta 
    const rolEsperado = route.data['role'];

    if (usuario.rol === rolEsperado) {
      return true; // pasa
    } else {
      alert('Acceso Denegado: No tienes permisos para ver esta pantalla.');
      return false; 
    }
  }

  // si no hay sesion
  alert('Por favor, inicia sesión primero.');
  router.navigate(['/login']);
  return false;
};