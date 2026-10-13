package controladores;

import dao.AsignacionMaestroDAO;
import dto.AsignacionRequestDTO;
import dto.AsignacionResponseDTO;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;
import java.util.List;

@Path("/asignaciones-docentes")
@Secured
public class AsignacionMaestroResource {

    private AsignacionMaestroDAO asignacionDAO = new AsignacionMaestroDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarAsignacion(AsignacionRequestDTO request) {
        if (request.getIdEmpleado() <= 0 || request.getIdCurso() <= 0
                || request.getIdSeccion() <= 0 || request.getIdCiclo() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Todos los identificadores (maestro, curso, sección, ciclo) son requeridos.\"}").build();
        }

        try {
            boolean registrado = asignacionDAO.registrarAsignacion(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Asignación de curso registrada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar la asignación.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/maestro/{idEmpleado}/ciclo/{idCiclo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarAsignacionesPorMaestro(
            @PathParam("idEmpleado") int idEmpleado,
            @PathParam("idCiclo") int idCiclo) {

        if (idEmpleado <= 0 || idCiclo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El ID del maestro y del ciclo son requeridos y válidos.\"}").build();
        }

        try {
            List<AsignacionResponseDTO> lista = asignacionDAO.consultarAsignacionesPorMaestro(idEmpleado, idCiclo);
            return Response.ok(lista).build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
