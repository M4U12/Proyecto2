package dao;

import dbconection.DBConnectionSingleton;
import dto.ReporteHistorialPagosDTO;
import dto.ReporteIngresosDTO;
import dto.ReporteLibrosMoraDTO;
import dto.ReporteRendimientoDTO;
import dto.ReporteSolvenciaDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    private PagoDAO pagoDAO = new PagoDAO();

    // Reporte de Estudiantes Solventes y Morosos
    public List<ReporteSolvenciaDTO> generarReporteSolvencia(int idGrado, int idSeccion, int idCiclo) throws BDException {
        List<ReporteSolvenciaDTO> lista = new ArrayList<>();

        // solo trae a los alumnos inscritos en ese grado/sección que estén activos
        String query = "SELECT e.id_estudiante, e.codigo_personal, e.nombres, e.apellidos "
                + "FROM inscripcion i "
                + "INNER JOIN estudiante e ON i.id_estudiante = e.id_estudiante "
                + "WHERE i.id_grado = ? AND i.id_seccion = ? AND i.id_ciclo = ? AND e.estado = 'ACTIVO' "
                + "ORDER BY e.apellidos ASC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idSeccion);
            ps.setInt(3, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteSolvenciaDTO dto = new ReporteSolvenciaDTO();
                    int idEstudiante = rs.getInt("id_estudiante");

                    dto.setCodigoPersonal(rs.getString("codigo_personal"));
                    dto.setNombreCompleto(rs.getString("apellidos") + ", " + rs.getString("nombres"));

                    String mensajeDeuda = pagoDAO.verificarSolvenciaEstudiante(idEstudiante, idCiclo);

                    if (mensajeDeuda == null || mensajeDeuda.isEmpty()) {
                        dto.setEstadoFinanciero("SOLVENTE");
                        dto.setDetalleDeuda("Al día con sus pagos");
                    } else {
                        dto.setEstadoFinanciero("MOROSO");
                        dto.setDetalleDeuda(mensajeDeuda);
                    }

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar los datos del reporte de solvencia: " + e.getMessage(), e);
        }

        return lista;
    }

    public List<ReporteRendimientoDTO> generarReporteRendimiento(int idGrado, int idSeccion, int idCiclo) throws BDException {
        List<ReporteRendimientoDTO> lista = new ArrayList<>();

        String baseQuery
                = "WITH NotasFinales AS ( "
                + //tabla temporal durante la consulta que va a contener lo que está dentro del parentesis
                "    SELECT "
                + "        c.id_curso, "
                + "        c.nombre AS curso, "
                + "        n.id_estudiante, "
                + "        SUM(n.punteo_obtenido) AS nota_final "
                + //suma las notas
                "    FROM curso c "
                + "    INNER JOIN asignacion_maestro am ON c.id_curso = am.id_curso "
                + "    INNER JOIN seccion s ON am.id_seccion = s.id_seccion "
                + "    INNER JOIN actividad a ON am.id_asignacion = a.id_asignacion "
                + "    INNER JOIN nota n ON a.id_actividad = n.id_actividad "
                + // solo toma la nota si el alumno está inscrito en esa sección
                "    INNER JOIN inscripcion i ON n.id_estudiante = i.id_estudiante "
                + "                             AND i.id_seccion = s.id_seccion "
                + "                             AND i.id_ciclo = s.id_ciclo "
                +//muestra si las igualdades se cumplen
                "    WHERE s.id_grado = ? AND s.id_ciclo = ? "; //para grado con todas las secciones y ciclo específico

        //filtro para sección en específico
        if (idSeccion > 0) {
            baseQuery += " AND s.id_seccion = ? ";
        }

        baseQuery
                += "    GROUP BY c.id_curso, c.nombre, n.id_estudiante "
                + ") "
                + "SELECT "
                + "    curso, "
                +//de la tabla temporal
                "    AVG(nota_final) AS promedio_general, "
                + //saca el promedio general de todas las notas finales del curso
                //cuenta cuantos alumnos tienen 60 o más, tambien si tienen menos, luego lo multiplica por cien y lo divide entre los alumnos
                "    (SUM(CASE WHEN nota_final >= 60 THEN 1 ELSE 0 END) * 100.0 / COUNT(id_estudiante)) AS porcentaje_aprobados, "
                + "    (SUM(CASE WHEN nota_final < 60 THEN 1 ELSE 0 END) * 100.0 / COUNT(id_estudiante)) AS porcentaje_reprobados "
                + "FROM NotasFinales "
                + "GROUP BY id_curso, curso "
                + "ORDER BY curso ASC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(baseQuery)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);

            if (idSeccion > 0) {
                ps.setInt(3, idSeccion);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteRendimientoDTO dto = new ReporteRendimientoDTO();
                    dto.setCurso(rs.getString("curso"));
                    dto.setPromedioGeneral(rs.getDouble("promedio_general"));
                    dto.setPorcentajeAprobados(rs.getDouble("porcentaje_aprobados"));
                    dto.setPorcentajeReprobados(rs.getDouble("porcentaje_reprobados"));
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar los datos de rendimiento: " + e.getMessage(), e);
        }

        return lista;
    }

    

    public List<ReporteIngresosDTO> generarReporteIngresos(String fechaInicio, String fechaFin) throws BDException {
        List<ReporteIngresosDTO> lista = new ArrayList<>();
        boolean usaFechas = (fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty());

        String query = 
            "SELECT concepto, COUNT(id_pago) AS cantidad, SUM(monto) AS subtotal " +
            "FROM pago " +
            "WHERE estado = 'ACTIVO' ";

        if (usaFechas) {
            query += " AND fecha_pago BETWEEN ? AND ? ";
        }

        //se agrupa por concepto para hacer el desglose y ordena por el que más dinero genera
        query += " GROUP BY concepto ORDER BY subtotal DESC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            if (usaFechas) {
                ps.setString(1, fechaInicio);
                ps.setString(2, fechaFin);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteIngresosDTO dto = new ReporteIngresosDTO();
                    dto.setConcepto(rs.getString("concepto"));
                    dto.setCantidadPagos(rs.getInt("cantidad"));
                    dto.setSubtotal(rs.getDouble("subtotal"));
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar el reporte de ingresos: " + e.getMessage(), e);
        }
        
        return lista;
    }


    public List<ReporteLibrosMoraDTO> generarReporteLibrosMora() throws BDException {
        List<ReporteLibrosMoraDTO> lista = new ArrayList<>();
        
        // filtra tanto los ACTIVOS vencidos como los DEVUELTOS que dejaron multa pendiente
        String query = 
            "SELECT p.id_prestamo, p.fecha_limite, p.estado AS estado_prestamo, p.multa_generada, p.fecha_devolucion, " +
            "l.codigo AS codigo_libro, l.titulo AS titulo_libro, " +
            "e.nombres AS est_nom, e.apellidos AS est_ape, " +
            "emp.nombres AS emp_nom, emp.apellidos AS emp_ape " +
            "FROM prestamo p " +
            "INNER JOIN libro l ON p.id_libro = l.id_libro " +
            "LEFT JOIN estudiante e ON p.id_estudiante = e.id_estudiante " +
            "LEFT JOIN empleado emp ON p.id_empleado = emp.id_empleado " +
            "WHERE (p.estado = 'ACTIVO' AND p.fecha_limite < CURRENT_DATE) " + 
            "   OR (p.estado = 'DEVUELTO' AND p.multa_generada > 0 AND p.multa_pagada = FALSE) " +
            "ORDER BY p.fecha_limite ASC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            double tarifaDiaria = new dao.PrestamoDAO().obtenerTarifaMora(conn);
            LocalDate hoy = LocalDate.now();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteLibrosMoraDTO dto = new ReporteLibrosMoraDTO();
                    dto.setTituloLibro(rs.getString("titulo_libro"));
                    dto.setCodigoLibro(rs.getString("codigo_libro"));
                    
                    if (rs.getObject("est_nom") != null) {
                        dto.setDeudor(rs.getString("est_nom") + " " + rs.getString("est_ape"));
                        dto.setTipoDeudor("ESTUDIANTE");
                    } else {
                        dto.setDeudor(rs.getString("emp_nom") + " " + rs.getString("emp_ape"));
                        dto.setTipoDeudor("MAESTRO");
                    }

                    java.sql.Date sqlFechaLimite = rs.getDate("fecha_limite");
                    dto.setFechaLimite(sqlFechaLimite.toString());
                    
                    String estadoBD = rs.getString("estado_prestamo");
                    
                    // para calcular la mora
                    if ("DEVUELTO".equals(estadoBD)) {
                        // si ya lo devolvió, toma la multa exacta que dice la base de datos
                        // y calcula los días de retraso basados en la fecha en que lo devolvió
                        java.sql.Date sqlFechaDevolucion = rs.getDate("fecha_devolucion");
                        long diasAtraso = 0;
                        if (sqlFechaDevolucion != null) {
                             diasAtraso = ChronoUnit.DAYS.between(sqlFechaLimite.toLocalDate(), sqlFechaDevolucion.toLocalDate());
                        }
                        dto.setDiasRetraso(diasAtraso > 0 ? (int) diasAtraso : 0);
                        dto.setRecargoGenerado(rs.getDouble("multa_generada"));
                        
                    } else {
                        // si está activo (aún no lo devuelve), calcula la mora al día de hoy
                        LocalDate limite = sqlFechaLimite.toLocalDate();
                        long diasAtraso = ChronoUnit.DAYS.between(limite, hoy);
                        dto.setDiasRetraso((int) diasAtraso);
                        dto.setRecargoGenerado(diasAtraso * tarifaDiaria);
                    }

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar reporte de libros en mora: " + e.getMessage(), e);
        }
        
        return lista;
    }
}
