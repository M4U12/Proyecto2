package servicios;

import dbconection.DBConnectionSingleton;
import dto.ReporteHistorialPagosDTO;
import dto.ReporteIngresosDTO;
import dto.ReporteLibrosMoraDTO;
import dto.ReporteRendimientoDTO;
import dto.ReporteSolvenciaDTO; 
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource; 
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.InputStream;
import java.sql.Connection;
import java.time.LocalDate; 
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

public class ReporteService {

    public byte[] generarFacturaPdf(String numeroFactura) throws Exception {
        // Obtener el archivo como InputStream
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/factura.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo factura.jrxml en resources/reportes/");
        }

        // Cargar el diseño usando JRXmlLoader 
        JasperDesign design = JRXmlLoader.load(jrxmlInput);

        //Compilar el reporte
        JasperReport report = JasperCompileManager.compileReport(design);

        //Mapear los parámetros
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("NUMERO_FACTURA", numeroFactura);

        // Llenar el reporte 
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn);
            
            //Exportar el resultado final a PDF en formato binario para JAX-RS
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
    
    public byte[] generarSolvenciaPdf(int idEstudiante, int idCiclo) throws Exception {
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/solvencia.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo solvencia.jrxml en resources/reportes/");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ID_ESTUDIANTE", idEstudiante);
        parameters.put("ID_CICLO", idCiclo);

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
    
    public byte[] generarReporteSolventesMorososPdf(List<ReporteSolvenciaDTO> listaAlumnos, String gradoSeccion, String cicloEscolar) throws Exception {
        
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/Reporte_Solvencia_Morosos.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo Reporte_Solvencia_Morosos.jrxml en resources/reportes/");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        // mapea los parametros del encabezado
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("GRADO_SECCION", gradoSeccion);
        parameters.put("CICLO_ESCOLAR", cicloEscolar);
        parameters.put("FECHA_GENERACION", LocalDate.now().toString());

        //convertir la lista a un formato que jasper entienda (DataSource)
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaAlumnos);

        //llena el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    public byte[] generarReporteRendimientoPdf(List<ReporteRendimientoDTO> listaCursos, String grado, String cicloEscolar) throws Exception {
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/Reporte_Rendimiento.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo Reporte_Rendimiento.jrxml en resources/reportes/");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("GRADO_SECCION", grado); // agrupa todas las secciones del grado
        parameters.put("CICLO_ESCOLAR", cicloEscolar);
        parameters.put("FECHA_GENERACION", LocalDate.now().toString());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaCursos);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    public byte[] generarHistorialPagosPdf(List<ReporteHistorialPagosDTO> lista, String nombre, String codigo, String periodo, String saldoPendiente) throws Exception {
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/Reporte_Historial_Pagos.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo Reporte_Historial_Pagos.jrxml en resources/reportes/");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("NOMBRE_ESTUDIANTE", nombre);
        parameters.put("CODIGO_PERSONAL", codigo);
        parameters.put("FECHA_GENERACION", LocalDate.now().toString());
        parameters.put("PERIODO", periodo);
        parameters.put("SALDO_PENDIENTE", saldoPendiente);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    public byte[] generarReporteIngresosPdf(List<ReporteIngresosDTO> lista, String periodoStr) throws Exception {
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/Reporte_Ingresos.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró el archivo Reporte_Ingresos.jrxml");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PERIODO", periodoStr);
        parameters.put("FECHA_GENERACION", LocalDate.now().toString());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
    public byte[] generarReporteLibrosMoraPdf(List<ReporteLibrosMoraDTO> lista) throws Exception {
        InputStream jrxmlInput = getClass().getResourceAsStream("/reportes/Reporte_Libros_Mora.jrxml");
        if (jrxmlInput == null) {
            throw new RuntimeException("No se encontró la plantilla Reporte_Libros_Mora.jrxml");
        }

        JasperDesign design = JRXmlLoader.load(jrxmlInput);
        JasperReport report = JasperCompileManager.compileReport(design);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("FECHA_GENERACION", LocalDate.now().toString());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
    
}