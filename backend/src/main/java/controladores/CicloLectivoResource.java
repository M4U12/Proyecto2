package controladores;

import dao.CicloLectivoDAO;
import dto.CrearCicloRequest;
import enums.EstadoCiclo;
import excepciones.BDException;
import modelos.CicloLectivo;
import seguridad.Secured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/ciclos")
@Secured
public class CicloLectivoResource {

    private CicloLectivoDAO cicloDAO = new CicloLectivoDAO();

    //Crear Ciclo Lectivo
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearCiclo(CrearCicloRequest request) {
        if (request.getAnio() < 2026) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El año lectivo debe ser un número positivo y mayor o igual a 2026.\"}").build();
        }
        
        LocalDate inicio = request.getFechaInicio();
        LocalDate fin = request.getFechaFin();

        if (inicio.isAfter(fin) || inicio.isEqual(fin)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La fecha de inicio debe ser estrictamente anterior a la fecha de fin.\"}").build();
        }

        if (inicio.getYear() != request.getAnio() || fin.getYear() != request.getAnio()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Las fechas ingresadas no corresponden al año lectivo " + request.getAnio() + ".\"}").build();
        }
        
        try {
            CicloLectivo nuevoCiclo = new CicloLectivo();
            nuevoCiclo.setAnio(request.getAnio());
            nuevoCiclo.setFechaInicio(request.getFechaInicio());
            nuevoCiclo.setFechaFin(request.getFechaFin());
            nuevoCiclo.setEstado(EstadoCiclo.CERRADO);

            boolean creado = cicloDAO.crearCiclo(nuevoCiclo);

            if (creado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Ciclo lectivo creado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo crear el ciclo lectivo.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Abrir Ciclo Lectivo
    @PUT
    @Path("/{id}/abrir")
    @Produces(MediaType.APPLICATION_JSON)
    public Response abrirCiclo(@PathParam("id") int idCiclo) {
        try {
            boolean abierto = cicloDAO.abrirCiclo(idCiclo);

            if (abierto) {
                return Response.ok("{\"mensaje\":\"Ciclo lectivo abierto exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Ciclo no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Cerrar Ciclo Lectivo
    @PUT
    @Path("/{id}/cerrar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cerrarCiclo(@PathParam("id") int idCiclo) {
        try {
            boolean cerrado = cicloDAO.cerrarCiclo(idCiclo);

            if (cerrado) {
                return Response.ok("{\"mensaje\":\"Ciclo lectivo cerrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Ciclo no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Obtener el Ciclo Activo 
    @GET
    @Path("/activo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerCicloActivo() {
        try {
            Optional<CicloLectivo> cicloOpt = cicloDAO.obtenerCicloActivo();

            if (cicloOpt.isPresent()) {
                return Response.ok(cicloOpt.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensaje\":\"Actualmente no hay ningún ciclo lectivo abierto.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    // Listar todos los ciclos paginados
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarCiclos(@QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {
        try {
            List<CicloLectivo> ciclos = cicloDAO.listarPaginado(pagina, limite);
            return Response.ok(ciclos).build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    
    // Modificar Ciclo Lectivo
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modificarCiclo(@PathParam("id") int idCiclo, CrearCicloRequest request) {
        if (request.getAnio() < 2026) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El año lectivo debe ser un número positivo y mayor o igual a 2026.\"}").build();
        }
        
        LocalDate inicio = request.getFechaInicio();
        LocalDate fin = request.getFechaFin();

        if (inicio.isAfter(fin) || inicio.isEqual(fin)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"La fecha de inicio debe ser estrictamente anterior a la fecha de fin.\"}").build();
        }

        if (inicio.getYear() != request.getAnio() || fin.getYear() != request.getAnio()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Las fechas ingresadas no corresponden al año lectivo " + request.getAnio() + ".\"}").build();
        }
        try {
            CicloLectivo ciclo = new CicloLectivo();
            ciclo.setIdCiclo(idCiclo);
            ciclo.setAnio(request.getAnio());
            ciclo.setFechaInicio(request.getFechaInicio()); 
            ciclo.setFechaFin(request.getFechaFin());

            boolean modificado = cicloDAO.modificarCiclo(ciclo);

            if (modificado) {
                return Response.ok("{\"mensaje\":\"Ciclo lectivo modificado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Ciclo no encontrado.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
