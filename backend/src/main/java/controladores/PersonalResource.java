package controladores;

import dao.EmpleadoDAO;
import dto.EstadoDTO;
import dto.RegistroPersonalDTO;
import enums.Rol;
import excepciones.BDException;
import modelos.Empleado;
import modelos.Usuario;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/personal")
@Secured
public class PersonalResource {

    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    // Registrar Empleado y Usuario
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarPersonal(RegistroPersonalDTO request) {
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
    public Response listarPersonal() {
        try {
            return Response.ok(empleadoDAO.listarPaginado(1, 100)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
