package controladores;

import dao.ReporteDAO;
import dto.ReporteHistorialPagosDTO;
import dto.ReporteIngresosDTO;
import dto.ReporteLibrosMoraDTO;
import dto.ReporteRendimientoDTO;
import dto.ReporteSolvenciaDTO;
import excepciones.BDException;
import servicios.ReporteService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
import seguridad.Secured;

@Path("/reportes")
@Secured
public class ReporteResource {

    private ReporteDAO reporteDAO = new ReporteDAO();
    private ReporteService reporteService = new ReporteService();

    @GET
    @Path("/solventes-morosos")
    @Produces("application/pdf")
    public Response descargarReporteSolvencia(
            @QueryParam("idGrado") int idGrado,
            @QueryParam("idSeccion") int idSeccion,
            @QueryParam("idCiclo") int idCiclo,
            @QueryParam("nombreGrado") String nombreGrado,
            @QueryParam("nombreSeccion") String nombreSeccion,
            @QueryParam("anioCiclo") String anioCiclo) {

        try {
            List<ReporteSolvenciaDTO> lista = reporteDAO.generarReporteSolvencia(idGrado, idSeccion, idCiclo);

            // formatea los textos para el encabezado del jasper
            String gradoSeccionStr = nombreGrado + " - Sección " + nombreSeccion;

            // arreglo de bytes del pdf
            byte[] pdfBytes = reporteService.generarReporteSolventesMorososPdf(lista, gradoSeccionStr, anioCiclo);

            //limpieza del nombre del archivo para que no tenga espacios raros
            String nombreArchivo = "Solvencia_" + nombreGrado.replaceAll("\\s+", "_") + "_" + nombreSeccion + ".pdf";

            // devuelve el pdf
            return Response.ok(pdfBytes)
                    .header("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"")
                    .build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al consultar la base de datos: " + e.getMessage())
                    .type("text/plain")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error interno al compilar JasperReports: " + e.getMessage())
                    .type("text/plain")
                    .build();
        }
    }

    @GET
    @Path("/rendimiento")
    @Produces("application/pdf")
    public Response descargarReporteRendimiento(
            @QueryParam("idGrado") int idGrado,
            @QueryParam("idSeccion") @DefaultValue("0") int idSeccion,
            @QueryParam("idCiclo") int idCiclo,
            @QueryParam("nombreGrado") String nombreGrado,
            @QueryParam("identificadorSeccion") String identificadorSeccion,
            @QueryParam("anioCiclo") String anioCiclo) {

        try {
            List<ReporteRendimientoDTO> lista = reporteDAO.generarReporteRendimiento(idGrado, idSeccion, idCiclo);
            String gradoSeccionStr = (idSeccion > 0 && identificadorSeccion != null && !identificadorSeccion.trim().isEmpty())
                    ? nombreGrado + " - Sección " + identificadorSeccion
                    : nombreGrado + " (General)";

            byte[] pdfBytes = reporteService.generarReporteRendimientoPdf(lista, gradoSeccionStr, anioCiclo);

            String nombreSec = (idSeccion > 0) ? "_" + identificadorSeccion : "_General";
            String nombreArchivo = "Rendimiento_" + nombreGrado.replaceAll("\\s+", "_") + nombreSec + ".pdf";

            return Response.ok(pdfBytes)
                    .header("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error JasperReports: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/ingresos")
    @Produces("application/pdf")
    public Response descargarReporteIngresos(
            @QueryParam("fechaInicio") String fechaInicio,
            @QueryParam("fechaFin") String fechaFin) {

        try {
            List<ReporteIngresosDTO> lista = reporteDAO.generarReporteIngresos(fechaInicio, fechaFin);

            boolean usaFechas = (fechaInicio != null && !fechaInicio.trim().isEmpty() && fechaFin != null && !fechaFin.trim().isEmpty());
            String periodoStr = usaFechas ? (fechaInicio + " al " + fechaFin) : "Histórico Completo";

            byte[] pdfBytes = reporteService.generarReporteIngresosPdf(lista, periodoStr);
            String nombreArchivo = "Ingresos_CodeAndSchool.pdf";

            return Response.ok(pdfBytes)
                    .header("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"")
                    .build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al consultar BD: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error JasperReports: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/mora")
    @Produces("application/pdf")
    public Response descargarReporteLibrosMora() {
        try {
            List<ReporteLibrosMoraDTO> lista = reporteDAO.generarReporteLibrosMora();
            
            byte[] pdfBytes = reporteService.generarReporteLibrosMoraPdf(lista);
            String nombreArchivo = "Libros_En_Mora.pdf";
            
            return Response.ok(pdfBytes)
                    .header("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"")
                    .build();

        } catch (BDException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al consultar BD: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error JasperReports: " + e.getMessage()).build();
        }
    }
}
