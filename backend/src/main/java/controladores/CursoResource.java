package controladores;

import dao.CursoDAO;
import dto.CursoRequestDTO;
import dto.EstadoDTO;
import excepciones.BDException;
import modelos.Curso;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/cursos")
public class CursoResource {
    private CursoDAO cursoDAO = new CursoDAO();

    // Registrar curso
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarCurso(CursoRequestDTO request) {
        try {
            Curso curso = new Curso();
            curso.setCodigo(request.getCodigo());
            curso.setNombre(request.getNombre());
            curso.setEstado(true); 

            boolean registrado = cursoDAO.crearCurso(curso);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Curso registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo registrar el curso.\"}").build();
            }
        } catch (BDException e) {
            // si se intenta meter un código repetido 
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Modificar curso
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarCurso(@PathParam("id") int idCurso, CursoRequestDTO request) {
        try {
            Curso curso = new Curso();
            curso.setIdCurso(idCurso);
            curso.setCodigo(request.getCodigo());
            curso.setNombre(request.getNombre());

            boolean modificado = cursoDAO.modificarCurso(curso);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Curso modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Curso no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    // Activar/Desactivar curso
    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstadoCurso(@PathParam("id") int idCurso, EstadoDTO request) {
        try {
            boolean cambiado = cursoDAO.cambiarEstado(idCurso, request.isEstado());

            if (cambiado) {
                String accion = request.isEstado() ? "activado" : "desactivado";
                return Response.ok("{\"mensaje\":\"Curso " + accion + " exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Curso no encontrado.\"}").build();
            }
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }


    // Listar cursos
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCursos(@QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {
        try {
            List<Curso> cursos = cursoDAO.listar(pagina, limite);
            return Response.ok(cursos).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
