package controladores;
import dao.EstudianteDAO;
import dto.EstudianteRequestDTO;
import dto.ExpedienteCompletoDTO;
import enums.EstadoEstudiante;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured; 
import java.util.Optional;
import dao.UsuarioDAO;

@Path("/estudiantes")
@Secured 
public class EstudianteResource {

    private EstudianteDAO estudianteDAO = new EstudianteDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarEstudiante(EstudianteRequestDTO request) {
        if (request.getCodigoPersonal() == null || request.getCodigoPersonal().trim().isEmpty()
                || request.getNombres() == null || request.getNombres().trim().isEmpty()
                || request.getApellidos() == null || request.getApellidos().trim().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El código personal, nombres y apellidos no pueden estar vacíos.\"}").build();
        }

        try {
            boolean registrado = estudianteDAO.registrarEstudiante(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Estudiante registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar al estudiante.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{codigo}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarEstudiante(@PathParam("codigo") String codigoPersonal, EstudianteRequestDTO request) {
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()
                || request.getApellidos() == null || request.getApellidos().trim().isEmpty()) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Nombres y apellidos no pueden estar vacíos.\"}").build();
        }

        try {
            boolean modificado = estudianteDAO.modificarEstudiante(codigoPersonal, request);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Estudiante modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Estudiante no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{codigo}/estado/{nuevoEstado}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoEstudiante(@PathParam("codigo") String codigoPersonal, @PathParam("nuevoEstado") String estadoStr) {
        try {
            EstadoEstudiante nuevoEstado = EstadoEstudiante.valueOf(estadoStr.toUpperCase());
            boolean cambiado = estudianteDAO.cambiarEstadoEstudiante(codigoPersonal, nuevoEstado);

            if (cambiado) {
                try {
                    // Primero necesitamos buscar el id_estudiante usando el código
                    Optional<ExpedienteCompletoDTO> expOpt = estudianteDAO.consultarExpediente(codigoPersonal);
                    if (expOpt.isPresent()) {
                        // OJO: Tendrás que agregar getIdEstudiante() a tu ExpedienteCompletoDTO
                        // O hacer una consulta directa en EstudianteDAO que retorne el ID numérico.
                        // Asumiendo que agregas el ID al DTO:
                        // int idEstudiante = expOpt.get().getIdEstudiante();
                        
                        // Lógica de acceso: Si está INACTIVO, se bloquea la cuenta. 
                        // Si está ACTIVO o GRADUADO, la cuenta funciona.
                        // boolean estadoCuenta = nuevoEstado != EstadoEstudiante.INACTIVO;
                        // usuarioDAO.cambiarEstadoPorEstudiante(idEstudiante, estadoCuenta);
                    }
                } catch (Exception e) {
                    System.err.println("Aviso: Falló la sincronización de cuenta del estudiante: " + e.getMessage());
                }

                return Response.ok("{\"mensaje\":\"Estado del estudiante actualizado exitosamente a " + nuevoEstado.name() + ".\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Estudiante no encontrado.\"}").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El estado especificado no es válido.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerExpedientePorCodigo(@PathParam("codigo") String codigoPersonal) {
        try {
            Optional<ExpedienteCompletoDTO> expedienteOpt = estudianteDAO.consultarExpediente(codigoPersonal);

            if (expedienteOpt.isPresent()) {
                return Response.ok(expedienteOpt.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Estudiante no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El parámetro de búsqueda no puede estar vacío.\"}").build();
        }
        
        try {
            return Response.ok(estudianteDAO.buscarCodigosPorNombre(nombre)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
