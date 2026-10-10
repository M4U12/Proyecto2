package controladores;

import dao.GradoDAO;
import dto.GradoRequestDTO;
import dto.SeccionDTO;
import excepciones.BDException;
import modelos.Grado;
import modelos.Seccion;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import seguridad.Secured;

@Path("/grados")
@Secured
public class GradoResource {

    private GradoDAO gradoDAO = new GradoDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarGrado(GradoRequestDTO request) {
        try {
            Grado gradoModelo = new Grado();
            gradoModelo.setNombre(request.getNombre());
            gradoModelo.setIdNivel(request.getIdNivel());
            gradoModelo.setIdCarrera(request.getIdCarrera());

            List<Seccion> seccionesModelo = new ArrayList<>();
            if (request.getSecciones() != null) {
                for (SeccionDTO secDTO : request.getSecciones()) {
                    Seccion seccion = new Seccion();
                    seccion.setIdentificador(secDTO.getIdentificador());
                    seccion.setCupo(secDTO.getCupo());
                    seccionesModelo.add(seccion);
                }
            }

            boolean registrado = gradoDAO.registrarGradoConSecciones(gradoModelo, seccionesModelo);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Grado y secciones registrados exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar el grado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar Grado
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarGrado(@PathParam("id") int idGrado, dto.ModificarGradoDTO request) {
        try {
            Grado grado = new Grado();
            grado.setIdGrado(idGrado);
            grado.setNombre(request.getNombre());
            grado.setIdNivel(request.getIdNivel());
            grado.setIdCarrera(request.getIdCarrera());

            boolean modificado = gradoDAO.modificarGrado(grado);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Grado modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Grado no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
