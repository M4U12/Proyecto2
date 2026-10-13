package controladores;

import dao.NivelEducativoDAO;
import dto.EstadoDTO;
import dto.ModificarNivelDTO;
import excepciones.BDException;
import modelos.NivelEducativo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import seguridad.Secured;

@Path("/niveles")
public class NivelEducativoResource {

    private NivelEducativoDAO nivelDAO = new NivelEducativoDAO();

    // Consultar niveles educativos
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarNiveles() {
        try {
            List<NivelEducativo> niveles = nivelDAO.listarTodos();
            return Response.ok(niveles).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar descripción del nivel
    @PUT
    @Secured
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarNivel(@PathParam("id") int idNivel, ModificarNivelDTO request) {
        try {
            NivelEducativo nivel = new NivelEducativo();
            nivel.setIdNivel(idNivel);
            nivel.setDescripcion(request.getDescripcion());

            boolean modificado = nivelDAO.modificarNivel(nivel);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Nivel educativo modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Nivel educativo no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Activar/Desactivar Nivel
    @PUT
    @Secured
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoNivel(@PathParam("id") int idNivel, EstadoDTO request) {
        try {
            boolean cambiado = nivelDAO.cambiarEstado(idNivel, request.isEstado());

            if (cambiado) {
                String accion = request.isEstado() ? "activado" : "desactivado";
                return Response.ok("{\"mensaje\":\"Nivel educativo " + accion + " exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Nivel educativo no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
