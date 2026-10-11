package controladores;

import dao.UsuarioDAO;
import excepciones.BDException;
import modelos.Usuario;
import dto.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import static java.util.UUID.randomUUID;

@Path("/auth")
public class AuthResource {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // llave parar firmar el token 
    // debe ser de al menos 256 bits, si angular intenta alterar el token, la firma no coincidirá.
    private static final String CLAVE_SECRETA = "IPC2_Proyecto2_ClaveSecreta_Para_JWT_Super_Segura_2026_xela";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(CLAVE_SECRETA.getBytes());

    // tiempo de expiración del token 
    private static final long TIEMPO_EXPIRACION = 8 * 60 * 60 * 1000;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest credenciales) {
        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(credenciales.getCorreo(), credenciales.getPassword());

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                if (!usuario.isEstado()) {
                    return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"La cuenta está desactivada.\"}")
                            .build();
                }

                String token = Jwts.builder()
                        .setSubject(usuario.getCorreo()) // a quien pertenece
                        .claim("idUsuario", usuario.getIdUsuario())
                        .claim("rol", usuario.getRol().name())
                        .claim("idEmpleado", usuario.getIdEmpleado())
                        .setIssuedAt(new Date()) // fecha emision
                        .setExpiration(new Date(System.currentTimeMillis() + TIEMPO_EXPIRACION)) // fecha de caducidad
                        .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                        .compact();

                // se envia a angular
                String jsonResponse = String.format(
                        "{\"token\":\"%s\", \"rol\":\"%s\", \"correo\":\"%s\"}",
                        token, usuario.getRol().name(), usuario.getCorreo()
                );

                return Response.ok(jsonResponse).build();

            } else {
                // si el correo o password están mal
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Credenciales incorrectas.\"}")
                        .build();
            }

        } catch (BDException e) {
            // Si la base de datos explota
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error en el servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/recuperar-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recuperarPassword(LoginRequest request) { 
        try {
            String correo = request.getCorreo();
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorCorreo(correo);

            if (!usuarioOpt.isPresent()) {
                // Por seguridad, siempre se devuelve OK para no revelar si un correo existe o no 
                return Response.ok("{\"mensaje\":\"Si el correo existe, se ha enviado un mensaje de recuperación.\"}").build();
            }

            if (!usuarioOpt.get().isEstado()) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\":\"La cuenta está desactivada. Contacte a administración.\"}").build();
            }

            // genera una clave temporal corta de 8 caracteres
            String claveTemporal = randomUUID().toString().substring(0, 8);

            // se guarda en la base de datos
            usuarioDAO.actualizarPasswordPorCorreo(correo, claveTemporal);

            // se envia el correo
            utilidades.EmailService.enviarClaveTemporal(correo, claveTemporal);

            return Response.ok("{\"mensaje\":\"Si el correo existe, se ha enviado un mensaje de recuperación.\"}").build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudo procesar la solicitud: " + e.getMessage() + "\"}").build();
        }
    }
}
