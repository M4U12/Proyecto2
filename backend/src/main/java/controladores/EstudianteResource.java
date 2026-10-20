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
import modelos.Estudiante;

@Path("/estudiantes")
@Secured
public class EstudianteResource {

    private EstudianteDAO estudianteDAO = new EstudianteDAO();

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

    @GET
    @Path("/asignacion/{idAsignacion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarAlumnosPorAsignacion(@PathParam("idAsignacion") int idAsignacion) {
        if (idAsignacion <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de asignación inválido.\"}").build();
        }
        try {
            return Response.ok(estudianteDAO.listarAlumnosPorAsignacion(idAsignacion)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/{id}/cursos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCursosDeEstudiante(@PathParam("id") int idEstudiante) {
        try {
            return Response.ok(estudianteDAO.listarCursosActivosPorEstudiante(idEstudiante)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPorId(@PathParam("id") int idEstudiante) {
        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de estudiante inválido.\"}").build();
        }

        try {
            Optional<Estudiante> estOpt = estudianteDAO.buscarPorId(idEstudiante);

            if (estOpt.isPresent()) {
                return Response.ok(estOpt.get()).build();
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarEstudiantes(@QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @QueryParam("filtro") String filtro){
        try {
            return Response.ok(estudianteDAO.listarEstudiantes(pagina, limite, filtro)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
