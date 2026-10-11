package controladores;

import dao.CurriculoDAO;
import dao.CursoDAO;
import dto.CurriculoRequestDTO;
import excepciones.BDException;
import modelos.Curso;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import seguridad.Secured;

@Path("/curriculos")
@Secured
public class CurriculoResource {
    private CurriculoDAO curriculoDAO = new CurriculoDAO();
    private CursoDAO cursoDAO = new CursoDAO();
    
    // Definir Currículo Obligatorio por Grado y Ciclo
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response definirCurriculo(CurriculoRequestDTO request) {
        try {
            // verifica que los cursos esten vigentes y activos
            if (request.getIdsCursos() != null && !request.getIdsCursos().isEmpty()) {
                for (Integer idCurso : request.getIdsCursos()) {
                    Optional<Curso> cursoOpt = cursoDAO.buscarPorId(idCurso);

                    if (!cursoOpt.isPresent()) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("{\"error\":\"El curso con ID " + idCurso + " no existe en el catálogo.\"}").build();
                    }
                    if (!cursoOpt.get().isEstado()) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity("{\"error\":\"El curso '" + cursoOpt.get().getNombre() + "' está inactivo y no puede asignarse al currículo.\"}").build();
                    }
                }
            }


            boolean exito = curriculoDAO.definirMallaCurricular(
                    request.getIdGrado(),
                    request.getIdCiclo(),
                    request.getIdsCursos()
            );

            if (exito) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Malla curricular definida exitosamente para el grado.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo guardar la malla curricular.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Consultar Currículo 
    @GET
    @Path("/grado/{idGrado}/ciclo/{idCiclo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCursosCurriculo(@PathParam("idGrado") int idGrado,
            @PathParam("idCiclo") int idCiclo) {
        try {
            List<Curso> cursos = curriculoDAO.obtenerCursosPorGradoYCiclo(idGrado, idCiclo);
            return Response.ok(cursos).build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/ciclo/{idCiclo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerMallaCompleta(@PathParam("idCiclo") int idCiclo) {
        try {
            List<dto.MallaCurricularDTO> malla = curriculoDAO.obtenerMallaCompletaPorCiclo(idCiclo);
            return Response.ok(malla).build();
        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
