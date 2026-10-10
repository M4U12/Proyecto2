package controladores;

import dao.SeccionDAO;
import dto.NuevaSeccionDTO;
import dto.SeccionDTO;
import excepciones.BDException;
import modelos.Seccion;

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

    // Agregar nueva sección a un grado
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarSeccion(NuevaSeccionDTO request) {
        try {
            // valida que la seccion no exista ya en ese grado
            Optional<Seccion> existente = seccionDAO.buscarPorGradoEIdentificador(request.getIdGrado(), request.getIdentificador());
            if (existente.isPresent()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"La sección '" + request.getIdentificador() + "' ya existe para este grado.\"}").build();
            }

            Seccion nuevaSeccion = new Seccion();
            nuevaSeccion.setIdentificador(request.getIdentificador());
            nuevaSeccion.setCupo(request.getCupo());
            nuevaSeccion.setIdGrado(request.getIdGrado());

            boolean registrado = seccionDAO.registrarSeccion(nuevaSeccion);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Sección agregada exitosamente.\"}").build();
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
            // REGLA DE NEGOCIO CU19: 
            // pendiente para la iteracion 2: comprueba que el nuevo cupo no sea menor a los alumnos ya inscritos en la sección."
            // con un COUNT de alumnos inscritos, si request.getCupo() < inscritos, se lanzara un BAD_REQUEST.

            Seccion seccion = new Seccion();
            seccion.setIdSeccion(idSeccion);
            seccion.setIdentificador(request.getIdentificador());
            seccion.setCupo(request.getCupo());

            boolean modificado = seccionDAO.modificarSeccion(seccion);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Sección modificada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Sección no encontrada.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    // EXTRA: Listar secciones de un grado específico
    @GET
    @Path("/grado/{idGrado}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarPorGrado(@PathParam("idGrado") int idGrado) {
        try {
            List<Seccion> secciones = seccionDAO.listarPorGrado(idGrado);
            return Response.ok(secciones).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
