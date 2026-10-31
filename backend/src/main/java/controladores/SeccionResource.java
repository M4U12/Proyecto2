package controladores;

import dao.SeccionDAO;
import dto.NuevaSeccionDTO;
import dto.SeccionDTO;
import excepciones.BDException;
import modelos.Seccion;
import dao.InscripcionDAO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import seguridad.Secured;

@Path("/secciones")
@Secured
public class SeccionResource {

    private SeccionDAO seccionDAO = new SeccionDAO();
    private InscripcionDAO inscripcionDAO = new InscripcionDAO();

    // Agregar nueva sección a un grado
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarSeccion(NuevaSeccionDTO request) {
        try {
            // validacion incluyendo el ciclo
            Optional<Seccion> existente = seccionDAO.buscarPorGradoCicloEIdentificador(request.getIdGrado(), request.getIdCiclo(), request.getIdentificador());
            if (existente.isPresent()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"La sección '" + request.getIdentificador() + "' ya existe para este grado en este ciclo escolar.\"}").build();
            }

            Seccion nuevaSeccion = new Seccion();
            nuevaSeccion.setIdentificador(request.getIdentificador());
            nuevaSeccion.setCupo(request.getCupo());
            nuevaSeccion.setIdGrado(request.getIdGrado());
            nuevaSeccion.setIdCiclo(request.getIdCiclo()); 

            boolean registrado = seccionDAO.registrarSeccion(nuevaSeccion);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Sección agregada exitosamente al ciclo.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar la sección.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar sección (cupo o identificador)
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarSeccion(@PathParam("id") int idSeccion, SeccionDTO request) {
        try {
            // obtiene la sección actual para saber a qué grado y ciclo pertenece
            Optional<Seccion> seccionActualOpt = seccionDAO.buscarPorId(idSeccion);
            if (!seccionActualOpt.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sección no encontrada.\"}").build();
            }
            Seccion seccionActual = seccionActualOpt.get();

            // validar que el nuevo identificador no exista ya en ese mismo grado/ciclo
            if (!seccionActual.getIdentificador().equalsIgnoreCase(request.getIdentificador().trim())) {
                Optional<Seccion> duplicado = seccionDAO.buscarPorGradoCicloEIdentificador(
                        seccionActual.getIdGrado(), seccionActual.getIdCiclo(), request.getIdentificador().trim());
                
                if (duplicado.isPresent() && duplicado.get().getIdSeccion() != idSeccion) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Ya existe una sección '" + request.getIdentificador() + "' en este grado y ciclo.\"}").build();
                }
            }

            // validar que no reduzcan el cupo más allá de los inscritos
            int inscritosActuales = inscripcionDAO.contarInscritosPorSeccion(idSeccion);
            if (request.getCupo() < inscritosActuales) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"El nuevo cupo (" + request.getCupo() + ") no puede ser menor a los ya inscritos (" + inscritosActuales + ").\"}")
                        .build();
            }

            Seccion seccion = new Seccion();
            seccion.setIdSeccion(idSeccion);
            seccion.setIdentificador(request.getIdentificador().trim());
            seccion.setCupo(request.getCupo());

            boolean modificado = seccionDAO.modificarSeccion(seccion);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Sección modificada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sección no encontrada al actualizar.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/grado/{idGrado}/ciclo/{idCiclo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarPorGradoYCiclo(@PathParam("idGrado") int idGrado, @PathParam("idCiclo") int idCiclo) {
        try {
            List<Seccion> secciones = seccionDAO.listarPorGradoYCiclo(idGrado, idCiclo);
            return Response.ok(secciones).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Eliminar sección
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarSeccion(@PathParam("id") int idSeccion) {
        try {
            boolean eliminado = seccionDAO.eliminarSeccion(idSeccion);
            if (eliminado) {
                return Response.ok("{\"mensaje\":\"Sección eliminada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sección no encontrada.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
