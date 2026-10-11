package seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Key;

@Provider
@Secured 
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private static final String CLAVE_SECRETA = "IPC2_Proyecto2_ClaveSecreta_Para_JWT_Super_Segura_2026_xela";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("---> EL FILTRO JWT SE ESTÁ EJECUTANDO PARA: " + requestContext.getUriInfo().getPath());
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }
        //  encabezado authorization de la petición
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validar que exista y empiece con "Bearer "
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Falta el token de autorización o es inválido.\"}").build());
            return;
        }

        //  Extraer el token quitando "Bearer "
        String token = authorizationHeader.substring(7);

        try {
            // Validar el token matemáticamente
            Key key = Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            // Si el token fue modificado, expiró, o es falso
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Token inválido o expirado.\"}").build());
        }
    }
}