package controladores;

import dao.LibroDAO;
import dto.EstadoDTO;
import dto.LibroRequestDTO;
import excepciones.BDException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/libros")
@Secured
public class LibroResource {

    private LibroDAO libroDAO = new LibroDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarLibro(LibroRequestDTO request) {
        if (request.getCodigo() == null || request.getTitulo() == null || request.getCopiasTotales() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Código, título y al menos 1 copia son requeridos.\"}").build();
        }

        try {
            boolean registrado = libroDAO.registrarLibro(request);
            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Libro registrado exitosamente en el catálogo.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar el libro.\"}").build();
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
    public Response modificarLibro(@PathParam("id") int idLibro, LibroRequestDTO request) {
        if (request.getCodigo() == null || request.getTitulo() == null || request.getCopiasTotales() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Código, título y al menos 1 copia son requeridos.\"}").build();
        }

        try {
            boolean modificado = libroDAO.modificarLibro(idLibro, request);
            if (modificado) {
                return Response.ok("{\"mensaje\":\"Datos del libro actualizados exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Libro no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoLibro(@PathParam("id") int idLibro, EstadoDTO request) {
        try {
            boolean cambiado = libroDAO.cambiarEstado(idLibro, request.isEstado());
            if (cambiado) {
                String accion = request.isEstado() ? "activado" : "desactivado";
                return Response.ok("{\"mensaje\":\"Libro " + accion + " exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Libro no encontrado.\"}").build();
            }
        } catch (BDException e) {
            // si hay copias prestadas
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarLibros(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @QueryParam("filtro") String filtro) {
        try {
            return Response.ok(libroDAO.listarLibros(pagina, limite, filtro)).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
