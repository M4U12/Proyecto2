package controladores;

import dao.NotaDAO;
import dto.NotaRequestDTO;
import excepciones.BDException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/notas")
@Secured
public class NotaResource {

    private NotaDAO notaDAO = new NotaDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarNota(NotaRequestDTO request) {
        if (request.getIdActividad() <= 0 || request.getIdEstudiante() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de actividad y de estudiante son obligatorios.\"}").build();
        }

        if (request.getPunteoObtenido() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El punteo no puede ser negativo.\"}").build();
        }

        try {
            boolean registrado = notaDAO.registrarNota(request);
            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Nota registrada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar la nota.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/punteo/{nuevoPunteo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarNota(
            @PathParam("id") int idNota,
            @PathParam("nuevoPunteo") double nuevoPunteo) {

        if (nuevoPunteo < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El punteo no puede ser negativo.\"}").build();
        }

        try {
            boolean modificado = notaDAO.modificarNota(idNota, nuevoPunteo);
            if (modificado) {
                return Response.ok("{\"mensaje\":\"Nota actualizada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Registro de nota no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/zona/estudiante/{idEstudiante}/asignacion/{idAsignacion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarZonaTotal(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAsignacion") int idAsignacion) {

        try {
            double zonaTotal = notaDAO.consultarZonaTotal(idEstudiante, idAsignacion);
            return Response.ok("{\"zonaTotal\": " + zonaTotal + "}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
