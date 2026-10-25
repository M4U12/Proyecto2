package controladores;

import dao.PrestamoDAO;
import dto.GestionPrestamoDTO;
import dto.PrestamoRequestDTO;
import dto.PrestamoResponseDTO;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import seguridad.Secured;

@Path("/prestamos")
@Secured
public class PrestamoResource {

    private PrestamoDAO prestamoDAO = new PrestamoDAO();

    @POST
    @Path("/solicitar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response solicitarPrestamo(PrestamoRequestDTO request) {
        if (request.getIdLibro() <= 0 || (request.getIdEstudiante() == null && request.getIdEmpleado() == null)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"ID de libro y un usuario (estudiante o empleado) son obligatorios.\"}").build();
        }
        try {
            if (prestamoDAO.solicitarPrestamo(request)) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Préstamo solicitado exitosamente.\"}").build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"No se pudo registrar la solicitud.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/aprobar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aprobarPrestamo(@PathParam("id") int idPrestamo, GestionPrestamoDTO request) {
        if (request.getFechaLimite() == null || request.getFechaLimite().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"La fecha límite es obligatoria.\"}").build();
        }
        try {
            if (prestamoDAO.aprobarPrestamo(idPrestamo, request.getFechaLimite())) {
                return Response.ok("{\"mensaje\":\"Préstamo aprobado y entregado exitosamente.\"}").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Solicitud no encontrada.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/rechazar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rechazarPrestamo(@PathParam("id") int idPrestamo) {
        try {
            if (prestamoDAO.rechazarPrestamo(idPrestamo)) {
                return Response.ok("{\"mensaje\":\"Solicitud de préstamo rechazada. Copia liberada.\"}").build();
            }
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Solicitud no encontrada.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/devolver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response devolverLibro(@PathParam("id") int idPrestamo, GestionPrestamoDTO request) {
        try {
            if (prestamoDAO.devolverLibro(idPrestamo, request.getMulta())) {
                return Response.ok("{\"mensaje\":\"Libro devuelto al inventario exitosamente.\"}").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Préstamo no encontrado.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/renovar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response renovarPrestamo(@PathParam("id") int idPrestamo, GestionPrestamoDTO request) {
        if (request.getFechaLimite() == null || request.getFechaLimite().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"La nueva fecha límite es obligatoria.\"}").build();
        }
        try {
            if (prestamoDAO.renovarPrestamo(idPrestamo, request.getFechaLimite())) {
                return Response.ok("{\"mensaje\":\"Préstamo renovado exitosamente.\"}").build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Préstamo no encontrado.\"}").build();
        } catch (BDException e) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarPrestamos(
            @QueryParam("idEstudiante") Integer idEstudiante,
            @QueryParam("idEmpleado") Integer idEmpleado,
            @QueryParam("estado") String estado,
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {
        try {
            return Response.ok(prestamoDAO.listarPrestamos(idEstudiante, idEmpleado, estado, pagina, limite)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/multas/pendientes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerMultasPendientes() {
        try {
            List<PrestamoResponseDTO> multas = prestamoDAO.listarMultasPendientes();
            return Response.ok(multas).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/pagar-multa")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarPagoMulta(@PathParam("id") int idPrestamo, dto.PagoMultaRequestDTO request) {

        if (request.getNumeroFactura() == null || request.getNumeroFactura().trim().isEmpty() || request.getIdCiclo() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El número de factura y el ciclo escolar son obligatorios.\"}").build();
        }

        try {
            boolean pagado = prestamoDAO.pagarMulta(idPrestamo, request.getNumeroFactura(), request.getIdCiclo());

            if (pagado) {
                return Response.ok("{\"mensaje\":\"Pago registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No se encontró el préstamo o ya estaba pagado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
