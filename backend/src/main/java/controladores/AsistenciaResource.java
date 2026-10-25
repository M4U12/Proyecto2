package controladores;

import dao.AsistenciaDAO;
import dto.AsistenciaRequestDTO;
import enums.EstadoAsistencia;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.format.DateTimeParseException;
import seguridad.Secured;

@Path("/asistencias")
@Secured
public class AsistenciaResource {

    private AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarAsistencia(AsistenciaRequestDTO request) {
        if (request.getIdEstudiante() <= 0 || request.getIdAsignacion() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Los IDs de estudiante y asignación son requeridos.\"}").build();
        }

        if (request.getFecha() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La fecha de asistencia es obligatoria.\"}").build();
        }

        if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El estado de la asistencia no puede estar vacío.\"}").build();
        }

        try {
            boolean registrado = asistenciaDAO.registrarAsistencia(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Asistencia registrada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar la asistencia.\"}").build();
            }

        } catch (BDException e) {
            // estado que no sea PRESENTE, AUSENTE o JUSTIFICADO
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/estado/{nuevoEstado}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarAsistencia(
            @PathParam("id") int idAsistencia,
            @PathParam("nuevoEstado") String estadoStr) {

        if (idAsistencia <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de asistencia inválido.\"}").build();
        }

        try {
            EstadoAsistencia nuevoEstado = EstadoAsistencia.valueOf(estadoStr.toUpperCase());
            boolean modificado = asistenciaDAO.modificarAsistencia(idAsistencia, nuevoEstado);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Asistencia actualizada exitosamente a " + nuevoEstado.name() + ".\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Registro de asistencia no encontrado.\"}").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Estado especificado no es válido. Opciones: PRESENTE, AUSENTE, JUSTIFICADO\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/asignacion/{idAsignacion}/fecha/{fecha}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarAsistencia(
            @PathParam("idAsignacion") int idAsignacion, 
            @PathParam("fecha") String fechaStr) {
        
        try {
            java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
            return Response.ok(asistenciaDAO.consultarAsistenciaPorFecha(idAsignacion, fecha)).build();
            
        } catch (DateTimeParseException e) {
             return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Formato de fecha inválido. Use YYYY-MM-DD.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
