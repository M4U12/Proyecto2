import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
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

  // Si no hay token pasa normal
  return next(req);
};