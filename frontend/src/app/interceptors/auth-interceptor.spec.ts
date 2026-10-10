import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Busca el usuario guardado en el localStorage
  const usuarioGuardado = localStorage.getItem('usuarioLogueado');
  let token = '';

  if (usuarioGuardado) {
    const usuario = JSON.parse(usuarioGuardado);
    token = usuario.token; 
  }

  if (token) {
    const peticionModificada = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(peticionModificada);
  }

  // Si no hay token envia la petición normal
  return next(req);
};