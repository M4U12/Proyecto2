package com.mycompany.backend.resources;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("api")
public class JakartaRestConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        
        // ¡Registramos el filtro de seguridad aquí!
        resources.add(seguridad.JwtFilter.class);
        
        // Registramos también tu filtro CORS
        resources.add(config.CorsFilter.class);
        
        // Tus controladores
        resources.add(controladores.AuthResource.class);
        resources.add(controladores.CarreraResource.class);
        resources.add(controladores.CicloLectivoResource.class);
        resources.add(controladores.CurriculoResource.class);
        resources.add(controladores.CursoResource.class);
        resources.add(controladores.GradoResource.class);
        resources.add(controladores.NivelEducativoResource.class);
        resources.add(controladores.PersonalResource.class);
        resources.add(controladores.SeccionResource.class);
        resources.add(controladores.UsuarioResource.class);
        
        return resources;
    }
}