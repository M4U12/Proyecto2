package controladores;

import dao.CarreraDAO;
import dto.CarreraRequestDTO;
import dto.EstadoDTO;
import excepciones.BDException;
import modelos.Carrera;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import seguridad.Secured;

@Path("/carreras")
@Secured
public class CarreraResource {

    private CarreraDAO carreraDAO = new CarreraDAO();

    // Registrar carrera
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarCarrera(CarreraRequestDTO request) {
        try {
            Carrera carrera = new Carrera();
            carrera.setCodigo(request.getCodigo());
            carrera.setNombre(request.getNombre());
            carrera.setDuracionAnios(request.getDuracionAnios());
            carrera.setEstado(true); 

            boolean registrado = carreraDAO.crearCarrera(carrera);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Carrera registrada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar la carrera.\"}").build();
            }
        } catch (BDException e) {
            // si el codigo ya existe, la BD lanzará error por el unique 
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar carrera
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarCarrera(@PathParam("id") int idCarrera, CarreraRequestDTO request) {
        try {
            Carrera carrera = new Carrera();
            carrera.setIdCarrera(idCarrera);
            carrera.setCodigo(request.getCodigo());
            carrera.setNombre(request.getNombre());
            carrera.setDuracionAnios(request.getDuracionAnios());

            boolean modificado = carreraDAO.modificarCarrera(carrera);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Carrera modificada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Carrera no encontrada.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Activar/desactivar carrera
    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoCarrera(@PathParam("id") int idCarrera, EstadoDTO request) {
        try {
            // por el momento no se comprueba que no tenga inscripciones en el ciclo escolar actual
            // es parte de la iteracion 2, se validara aquí antes de proceder a desactivarla (request.isEstado() == false)

            boolean cambiado = carreraDAO.cambiarEstado(idCarrera, request.isEstado());

            if (cambiado) {
                String accion = request.isEstado() ? "activada" : "desactivada";
                return Response.ok("{\"mensaje\":\"Carrera " + accion + " exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Carrera no encontrada.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Listar carreras paginadas
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCarreras(@QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {
        try {
            List<Carrera> carreras = carreraDAO.listar(pagina, limite);
            return Response.ok(carreras).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
