package controladores;

import dao.EmpleadoDAO;
import dao.UsuarioDAO;
import dto.EstadoDTO;
import dto.RegistroPersonalDTO;
import enums.Rol;
import excepciones.BDException;
import modelos.Empleado;
import modelos.Usuario;
import java.time.LocalDate;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/personal")
@Secured
public class PersonalResource {

    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Registrar Empleado y Usuario
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarPersonal(RegistroPersonalDTO request) {
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()
                || request.getApellidos() == null || request.getApellidos().trim().isEmpty()
                || request.getDpi() == null || request.getDpi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El DPI, nombres y apellidos no pueden estar vacíos.\"}").build();
        }
        if (request.getSalario() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El salario no puede ser negativo.\"}").build();
        }
        try {
            Empleado empleado = new Empleado();
            empleado.setDpi(request.getDpi());
            empleado.setNombres(request.getNombres());
            empleado.setApellidos(request.getApellidos());
            empleado.setPuesto(request.getPuesto());
            empleado.setFechaContratacion(request.getFechaContratacion());
            empleado.setSalario(request.getSalario());
            empleado.setEstado(true);

            boolean registrado;
            // si viene un correo, crea la cuenta 
            if (request.getCorreo() != null && !request.getCorreo().trim().isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setCorreo(request.getCorreo());
                usuario.setPassword(request.getPassword());
                usuario.setRol(Rol.valueOf(request.getRol()));
                usuario.setEstado(true);

                registrado = empleadoDAO.registrarEmpleadoConUsuario(empleado, usuario);
            } else {
                // Si no hay correo solo guarda al empleado en la nomina
                registrado = empleadoDAO.registrarEmpleado(empleado);
            }

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Personal registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar al empleado.\"}").build();
            }

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El rol especificado no es válido.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Activar/Desactivar Empleado
    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoEmpleado(@PathParam("id") int idEmpleado, EstadoDTO request) {
        try {
            boolean cambiado = empleadoDAO.cambiarEstado(idEmpleado, request.isEstado());

            if (cambiado) {
                try {
                    // Nota: Si aún no tienes este método en UsuarioDAO, tendrás que crearlo
                    // (Te lo adjunto más abajo).
                    usuarioDAO.cambiarEstadoPorEmpleado(idEmpleado, request.isEstado());
                } catch (Exception e) {
                    System.err.println("Aviso: El empleado no tenía usuario o falló la actualización de cuenta: " + e.getMessage());
                }

                String accion = request.isEstado() ? "activado" : "desactivado";
                return Response.ok("{\"mensaje\":\"Empleado " + accion + " en nómina exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Empleado no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarPersonal(@QueryParam("pagina") @DefaultValue("1") int pagina,
                                   @QueryParam("limite") @DefaultValue("10") int limite) {
        try {
            return Response.ok(empleadoDAO.listarPaginado(pagina, limite)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarPersonal(@PathParam("id") int idEmpleado, RegistroPersonalDTO request) {
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()
                || request.getApellidos() == null || request.getApellidos().trim().isEmpty()
                || request.getDpi() == null || request.getDpi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El DPI, nombres y apellidos no pueden estar vacíos.\"}").build();
        }
        if (request.getSalario() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El salario no puede ser negativo.\"}").build();
        }
        try {
            Empleado empleado = new Empleado();
            empleado.setIdEmpleado(idEmpleado);
            empleado.setDpi(request.getDpi());
            empleado.setNombres(request.getNombres());
            empleado.setApellidos(request.getApellidos());
            empleado.setPuesto(request.getPuesto());
            empleado.setFechaContratacion(request.getFechaContratacion());
            empleado.setSalario(request.getSalario());

            boolean modificado = empleadoDAO.modificarEmpleado(empleado);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Empleado modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Empleado no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPersonalPorId(@PathParam("id") int idEmpleado) {
        try {
            java.util.Optional<Empleado> empleadoOpt = empleadoDAO.buscarPorId(idEmpleado);
            
            if (empleadoOpt.isPresent()) {
                return Response.ok(empleadoOpt.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Empleado no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
