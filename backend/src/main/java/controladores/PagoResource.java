package controladores;

import dao.PagoDAO;
import dto.PagoRequestDTO;
import enums.ConceptoPago;
import excepciones.BDException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import seguridad.Secured;

@Path("/pagos")
@Secured
public class PagoResource {

    private PagoDAO pagoDAO = new PagoDAO();

    @POST
    @Path("/inscripcion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cobrarInscripcion(PagoRequestDTO request) {
        if (request.getNumeroFactura() == null || request.getNumeroFactura().trim().isEmpty()
                || request.getIdEstudiante() <= 0 || request.getIdCiclo() <= 0 || request.getMonto() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Faltan datos obligatorios (Factura, Estudiante, Ciclo o Monto).\"}").build();
        }

        try {
            boolean yaPagado = pagoDAO.verificarInscripcionPagada(request.getIdEstudiante(), request.getIdCiclo());
            if (yaPagado) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"El estudiante ya tiene registrada la inscripción para este ciclo escolar.\"}").build();
            }

            // Asignación segura con Enum
            request.setConcepto(ConceptoPago.INSCRIPCION);

            boolean registrado = pagoDAO.registrarPago(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Pago de inscripción registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo completar el registro del pago.\"}").build();
            }

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @PUT
    @Path("/anular/{numeroFactura}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response anularFactura(@PathParam("numeroFactura") String numeroFactura, dto.AnulacionRequestDTO request) {

        if (numeroFactura == null || numeroFactura.trim().isEmpty()
                || request.getMotivoAnulacion() == null || request.getMotivoAnulacion().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"El número de factura y el motivo de anulación son campos obligatorios.\"}").build();
        }

        try {
            boolean anulado = pagoDAO.anularPago(numeroFactura, request.getMotivoAnulacion());

            if (anulado) {
                return Response.ok("{\"mensaje\":\"Factura anulada exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"No se pudo anular la factura.\"}").build();
            }
        } catch (BDException e) {
            int status = e.getMessage().contains("ya se encuentra anulada") || e.getMessage().contains("No se encontró") ? 400 : 500;
            return Response.status(status)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/general")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cobrarPagoGeneral(PagoRequestDTO request) {
        if (request.getNumeroFactura() == null || request.getNumeroFactura().trim().isEmpty()
                || request.getIdEstudiante() <= 0 || request.getIdCiclo() <= 0 || request.getMonto() <= 0 || request.getConcepto() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Faltan datos obligatorios para registrar el cobro.\"}").build();
        }

        try {
            // Si es colegiatura, validar mes duplicado
            if (request.getConcepto() == ConceptoPago.COLEGIATURA) {
                if (request.getMesColegiatura() == null || request.getMesColegiatura() < 1 || request.getMesColegiatura() > 12) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\":\"Debe especificar un mes válido (1-12) para el cobro de colegiatura.\"}").build();
                }

                boolean yaPagado = pagoDAO.verificarColegiaturaPagada(request.getIdEstudiante(), request.getIdCiclo(), request.getMesColegiatura());
                if (yaPagado) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity("{\"error\":\"El estudiante ya pagó la colegiatura de ese mes en este ciclo escolar.\"}").build();
                }
            } else {
                // Si es extracurricular u otro, se asegura de que no guarde un mes accidentalmente
                request.setMesColegiatura(null);
            }

            boolean registrado = pagoDAO.registrarPago(request);

            if (registrado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\":\"Cobro registrado exitosamente.\"}").build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\":\"No se pudo completar el registro del pago.\"}").build();
            }

        } catch (BDException e) {
            int status = e.getMessage().contains("ya está registrado") ? 409 : 500;
            return Response.status(status).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
