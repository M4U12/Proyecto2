package controladores;

import dao.ActividadDAO;
import dto.ActividadRequestDTO;
import excepciones.BDException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/actividades")
@Secured
public class ActividadResource {

    private ActividadDAO actividadDAO = new ActividadDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearActividad(ActividadRequestDTO request) {
        if (request.getIdAsignacion() <= 0 || request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de asignación y nombre de actividad son requeridos.\"}").build();
        }

        if (request.getPonderacion() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La ponderación debe ser mayor a 0.\"}").build();
        }

        try {
            boolean creado = actividadDAO.crearActividad(request);
            if (creado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Actividad creada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo crear la actividad.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarActividad(@PathParam("id") int idActividad, ActividadRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty() || request.getPonderacion() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Nombre válido y ponderación mayor a 0 son requeridos.\"}").build();
        }

        try {
            boolean modificado = actividadDAO.modificarActividad(idActividad, request);
            if (modificado) {
                return Response.ok("{\"mensaje\":\"Actividad modificada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Actividad no encontrada.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/asignacion/{idAsignacion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarActividades(@PathParam("idAsignacion") int idAsignacion) {
        try {
            return Response.ok(actividadDAO.listarActividadesPorAsignacion(idAsignacion)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
