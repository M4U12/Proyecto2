package controladores;
import dao.InscripcionDAO;
import dto.InscripcionRequestDTO;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/inscripciones")
@Secured 
public class InscripcionResource {

    private InscripcionDAO inscripcionDAO = new InscripcionDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response inscribirEstudiante(InscripcionRequestDTO request) {
        if (request.getIdEstudiante() <= 0 || request.getIdGrado() <= 0
                || request.getIdSeccion() <= 0 || request.getIdCiclo() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Todos los identificadores (estudiante, grado, sección, ciclo) son requeridos y deben ser válidos.\"}").build();
        }

        try {
            if (!inscripcionDAO.hayCupoDisponible(request.getIdSeccion())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"No hay cupo disponible en la sección seleccionada.\"}").build();
            }

            boolean registrado = inscripcionDAO.inscribirEstudiante(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Estudiante inscrito exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo completar la inscripción.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/estudiante/{idEstudiante}/ciclo/{idCiclo}/nueva-seccion/{idNuevaSeccion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reasignarSeccion(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idCiclo") int idCiclo,
            @PathParam("idNuevaSeccion") int idNuevaSeccion) {

        if (idEstudiante <= 0 || idCiclo <= 0 || idNuevaSeccion <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Los identificadores proporcionados no son válidos.\"}").build();
        }

        try {
            if (!inscripcionDAO.hayCupoDisponible(idNuevaSeccion)) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"No hay cupo disponible en la nueva sección.\"}").build();
            }

            boolean reasignado = inscripcionDAO.reasignarSeccion(idEstudiante, idCiclo, idNuevaSeccion);

            if (reasignado) {
                return Response.ok("{\"mensaje\":\"Estudiante reasignado de sección exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No se encontró una inscripción vigente para reasignar.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
