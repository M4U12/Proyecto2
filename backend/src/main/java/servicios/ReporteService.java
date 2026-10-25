package servicios;

import dbconection.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
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
}