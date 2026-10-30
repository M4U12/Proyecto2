package controladores;

import dao.ConfiguracionDAO;
import dto.ConfiguracionDTO;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import seguridad.Secured;

@Path("/configuraciones")
@Secured 
public class ConfiguracionResource {

    private ConfiguracionDAO dao = new ConfiguracionDAO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarConfiguraciones() {
        try {
            List<ConfiguracionDTO> configs = dao.listarConfiguraciones();
            return Response.ok(configs).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{clave}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarConfiguracion(@PathParam("clave") String clave, ConfiguracionDTO request) {
        if (request.getValor() == null || request.getValor() < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El valor no puede ser negativo ni nulo.\"}").build();
        }

        try {
            boolean actualizado = dao.actualizarValor(clave, request.getValor());
            if (actualizado) {
                return Response.ok("{\"mensaje\":\"Configuración actualizada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No se encontró la configuración.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
