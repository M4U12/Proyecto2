package com.mycompany.backend;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("api")
public class JakartaRestConfiguration extends Application {
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(seguridad.JwtFilter.class); 
        resources.add(config.CorsFilter.class);
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
        resources.add(controladores.LibroResource.class);
        
        return resources;
    }
}
