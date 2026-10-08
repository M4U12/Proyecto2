package config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {

        // permite que cualquier dominio haga peticiones
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        // permite el envio de credenciales y tokens de autorización
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        // define que cabeceras puede enviar angular 
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");

        // define que metodos HTTP estan permitidos
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
