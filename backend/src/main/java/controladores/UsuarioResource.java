package controladores;

import dao.UsuarioDAO;
import dto.CambiarPasswordRequest;
import dto.EstadoDTO;
import dto.ModificarPerfilRequest;
import dto.ModificarUsuarioDTO;
import excepciones.BDException;
import modelos.Usuario;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import java.util.Base64;
import seguridad.Secured;

@Path("/usuarios")
@Secured
public class UsuarioResource {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Modificar Perfil
    @PUT
    @Path("/{id}/perfil")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarPerfil(@PathParam("id") int idUsuario, ModificarPerfilRequest request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setCorreo(request.getCorreo());

            boolean modificado = usuarioDAO.modificarPerfil(usuario);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Perfil actualizado correctamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Cambiar Contraseña
    @PUT
    @Path("/{id}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarPassword(@PathParam("id") int idUsuario, CambiarPasswordRequest request) {
        try {
            Optional<String> passwordOpt = usuarioDAO.buscarPasswordPorId(idUsuario);

            if (!passwordOpt.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado.\"}").build();
            }

            String pass = passwordOpt.get();
            String passwordActualCodificada = Base64.getEncoder().encodeToString(request.getPasswordActual().getBytes());

            if (!passwordActualCodificada.equals(pass)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"La contraseña actual ingresada es incorrecta.\"}").build();
            }

            boolean actualizado = usuarioDAO.actualizarPassword(idUsuario, request.getPasswordNueva());

            if (actualizado) {
                return Response.ok("{\"mensaje\":\"Contraseña cambiada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo actualizar la contraseña.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error de base de datos: " + e.getMessage() + "\"}").build();
        }
    }

    // Activar/Desactivar Usuario
    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoUsuario(@PathParam("id") int idUsuario, EstadoDTO request) {
        try {
            boolean cambiado = usuarioDAO.cambiarEstado(idUsuario, request.isEstado());

            if (cambiado) {
                String accion = request.isEstado() ? "activado" : "desactivado";
                return Response.ok("{\"mensaje\":\"Usuario " + accion + " exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar usuario operativo
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarUsuario(@PathParam("id") int idUsuario, ModificarUsuarioDTO request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setCorreo(request.getCorreo());
            usuario.setRol(enums.Rol.valueOf(request.getRol()));

            usuario.setIdEmpleado(request.getIdEmpleado());
            usuario.setIdEstudiante(request.getIdEstudiante());

            boolean modificado = usuarioDAO.modificarUsuario(usuario);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Usuario operativo modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Usuario no encontrado.\"}").build();
            }

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El rol especificado no es válido.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @POST
    @Path("/estudiante")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearUsuarioEstudiante(dto.RegistroUsuarioEstudianteDTO request) {
        if (request.getIdEstudiante() <= 0 || request.getCorreo() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de estudiante, correo y contraseña son obligatorios.\"}").build();
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setCorreo(request.getCorreo());
            usuario.setPassword(request.getPassword());
            usuario.setRol(enums.Rol.ESTUDIANTE);
            usuario.setEstado(true);
            usuario.setIdEstudiante(request.getIdEstudiante());

            boolean registrado = usuarioDAO.registrarUsuario(usuario);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Credenciales de estudiante creadas exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo crear el usuario.\"}").build();
            }

        } catch (BDException e) {
            // si el correo ya existe en la bd saltará la excepción
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
