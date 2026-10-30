package dao;

import dbconection.DBConnectionSingleton;
import dto.PrestamoRequestDTO;
import dto.PrestamoResponseDTO;
import excepciones.BDException;
import enums.EstadoPrestamo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    public boolean solicitarPrestamo(PrestamoRequestDTO request) throws BDException {
        // valida si es moroso antes de cualquier cosa
        if (tieneMorasOPendientes(request.getIdEstudiante(), request.getIdEmpleado())) {
            throw new BDException("Solicitud denegada: El usuario tiene libros vencidos sin devolver o multas pendientes de pago.");
        }

        String checkLibro = "SELECT copias_disponibles FROM libro WHERE id_libro = ? AND estado = 'ACTIVO' FOR UPDATE";
        String insertPrestamo = "INSERT INTO prestamo (id_libro, id_estudiante, id_empleado, fecha_solicitud, fecha_limite, estado) VALUES (?, ?, ?, ?, ?, ?)";
        String updateLibro = "UPDATE libro SET copias_disponibles = copias_disponibles - 1 WHERE id_libro = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                // verifica disponibilidad de copias
                try (PreparedStatement psCheck = conn.prepareStatement(checkLibro)) {
                    psCheck.setInt(1, request.getIdLibro());
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (!rs.next() || rs.getInt("copias_disponibles") <= 0) {
                            throw new BDException("No hay copias disponibles de este libro en este momento.");
                        }
                    }
                }

                // pone el prestamo en estado SOLICITADO
                LocalDate hoy = LocalDate.now();

                try (PreparedStatement psInsert = conn.prepareStatement(insertPrestamo)) {
                    psInsert.setInt(1, request.getIdLibro());

                    if (request.getIdEstudiante() != null) {
                        psInsert.setInt(2, request.getIdEstudiante());
                    } else {
                        psInsert.setNull(2, Types.INTEGER);
                    }

                    if (request.getIdEmpleado() != null) {
                        psInsert.setInt(3, request.getIdEmpleado());
                    } else {
                        psInsert.setNull(3, Types.INTEGER);
                    }

                    psInsert.setDate(4, Date.valueOf(hoy));
                    psInsert.setDate(5, Date.valueOf(hoy));

                    psInsert.setString(6, EstadoPrestamo.SOLICITADO.name());
                    psInsert.executeUpdate();
                }

                // apartar la copia
                try (PreparedStatement psUpdate = conn.prepareStatement(updateLibro)) {
                    psUpdate.setInt(1, request.getIdLibro());
                    psUpdate.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException | BDException e) {
            throw new BDException("Error al registrar la solicitud: " + e.getMessage(), e);
        }
    }

    // Aceptar entrega (Bibliotecario)
    public boolean aprobarPrestamo(int idPrestamo, String fechaLimite) throws BDException {
        String query = "UPDATE prestamo SET estado = 'ACTIVO', fecha_prestamo = ?, fecha_limite = ? WHERE id_prestamo = ? AND estado = 'SOLICITADO'";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setDate(2, Date.valueOf(fechaLimite));
            ps.setInt(3, idPrestamo);

            if (ps.executeUpdate() == 0) {
                throw new BDException("No se encontró la solicitud o ya fue procesada.");
            }
            return true;
        } catch (SQLException e) {
            throw new BDException("Error al aprobar préstamo: " + e.getMessage(), e);
        }
    }

    // Cancelar/Rechazar solicitud (Bibliotecario)
    public boolean rechazarPrestamo(int idPrestamo) throws BDException {
        String selectLibro = "SELECT id_libro FROM prestamo WHERE id_prestamo = ? AND estado = 'SOLICITADO'";
        String updatePrestamo = "UPDATE prestamo SET estado = 'RECHAZADO' WHERE id_prestamo = ?";
        String updateLibro = "UPDATE libro SET copias_disponibles = copias_disponibles + 1 WHERE id_libro = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                int idLibro = 0;
                try (PreparedStatement psSelect = conn.prepareStatement(selectLibro)) {
                    psSelect.setInt(1, idPrestamo);
                    try (ResultSet rs = psSelect.executeQuery()) {
                        if (rs.next()) {
                            idLibro = rs.getInt("id_libro");
                        } else {
                            throw new BDException("La solicitud no existe o no está en estado SOLICITADO.");
                        }
                    }
                }

                try (PreparedStatement psUpdateP = conn.prepareStatement(updatePrestamo)) {
                    psUpdateP.setInt(1, idPrestamo);
                    psUpdateP.executeUpdate();
                }

                // se devuelve la copia al stock
                try (PreparedStatement psUpdateL = conn.prepareStatement(updateLibro)) {
                    psUpdateL.setInt(1, idLibro);
                    psUpdateL.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new BDException("Error al rechazar préstamo: " + e.getMessage(), e);
        }
    }

    // Registrar devolución y multas
    public boolean devolverLibro(int idPrestamo, double multaAplicada) throws BDException {
        String select = "SELECT id_libro FROM prestamo WHERE id_prestamo = ? AND estado = 'ACTIVO'";
        String updatePrestamo = "UPDATE prestamo SET estado = 'DEVUELTO', fecha_devolucion = ?, multa_generada = ? WHERE id_prestamo = ?";
        String updateLibro = "UPDATE libro SET copias_disponibles = copias_disponibles + 1 WHERE id_libro = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try {
                int idLibro = 0;
                try (PreparedStatement ps = conn.prepareStatement(select)) {
                    ps.setInt(1, idPrestamo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            idLibro = rs.getInt("id_libro");
                        } else {
                            throw new BDException("Préstamo no válido o ya fue devuelto.");
                        }
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(updatePrestamo)) {
                    ps.setDate(1, Date.valueOf(LocalDate.now()));
                    ps.setDouble(2, multaAplicada);
                    ps.setInt(3, idPrestamo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(updateLibro)) {
                    ps.setInt(1, idLibro);
                    ps.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new BDException("Error al procesar devolución: " + e.getMessage(), e);
        }
    }

    public boolean renovarPrestamo(int idPrestamo, String nuevaFecha) throws BDException {
        String select = "SELECT renovaciones FROM prestamo WHERE id_prestamo = ? AND estado = 'ACTIVO'";
        String update = "UPDATE prestamo SET renovaciones = renovaciones + 1, fecha_limite = ? WHERE id_prestamo = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            try (PreparedStatement psSelect = conn.prepareStatement(select)) {
                psSelect.setInt(1, idPrestamo);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("renovaciones") >= 2) {
                            throw new BDException("Límite de renovaciones alcanzado (Máximo 2 veces).");
                        }
                    } else {
                        throw new BDException("No se puede renovar este préstamo.");
                    }
                }
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(update)) {
                psUpdate.setDate(1, Date.valueOf(nuevaFecha));
                psUpdate.setInt(2, idPrestamo);
                return psUpdate.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new BDException("Error al renovar préstamo: " + e.getMessage(), e);
        }
    }

    public boolean tieneMorasOPendientes(Integer idEstudiante, Integer idEmpleado) throws BDException {
        // busca prestamos activos vencidos O multas sin pagar
        String query = "SELECT COUNT(*) AS moras FROM prestamo WHERE "
                + "(id_estudiante = ? OR id_empleado = ?) AND "
                + "((estado = 'ACTIVO' AND fecha_limite < CURRENT_DATE) OR "
                + "(multa_generada > 0 AND multa_pagada = FALSE))";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setObject(1, idEstudiante, Types.INTEGER);
            ps.setObject(2, idEmpleado, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("moras") > 0;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar historial de morosidad: " + e.getMessage(), e);
        }
        return false;
    }

    public List<PrestamoResponseDTO> listarPrestamos(Integer idEstudiante, Integer idEmpleado, String estadoFiltro, int pagina, int limite) throws BDException {
        List<PrestamoResponseDTO> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;

        StringBuilder sql = new StringBuilder(
                "SELECT p.*, l.codigo AS codigo_libro, l.titulo AS titulo_libro, "
                + "e.nombres AS est_nom, e.apellidos AS est_ape, "
                + "emp.nombres AS emp_nom, emp.apellidos AS emp_ape "
                + "FROM prestamo p "
                + "INNER JOIN libro l ON p.id_libro = l.id_libro "
                + "LEFT JOIN estudiante e ON p.id_estudiante = e.id_estudiante "
                + "LEFT JOIN empleado emp ON p.id_empleado = emp.id_empleado "
                + "WHERE 1=1 "
        );

        if (idEstudiante != null) {
            sql.append("AND p.id_estudiante = ? ");
        }
        if (idEmpleado != null) {
            sql.append("AND p.id_empleado = ? ");
        }
        if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
            sql.append("AND p.estado = ? ");
        }

        sql.append("ORDER BY p.id_prestamo DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            double tarifaMora = obtenerTarifaMora(conn);
            int paramIndex = 1;
            if (idEstudiante != null) {
                ps.setInt(paramIndex++, idEstudiante);
            }
            if (idEmpleado != null) {
                ps.setInt(paramIndex++, idEmpleado);
            }
            if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
                ps.setString(paramIndex++, estadoFiltro);
            }

            ps.setInt(paramIndex++, limite);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PrestamoResponseDTO dto = new PrestamoResponseDTO();
                    dto.setIdPrestamo(rs.getInt("id_prestamo"));
                    dto.setIdLibro(rs.getInt("id_libro"));
                    dto.setCodigoLibro(rs.getString("codigo_libro"));
                    dto.setTituloLibro(rs.getString("titulo_libro"));

                    if (rs.getObject("id_estudiante") != null) {
                        dto.setNombreSolicitante(rs.getString("est_nom") + " " + rs.getString("est_ape"));
                        dto.setTipoSolicitante("ESTUDIANTE");
                    } else {
                        dto.setNombreSolicitante(rs.getString("emp_nom") + " " + rs.getString("emp_ape"));
                        dto.setTipoSolicitante("MAESTRO");
                    }

                    if (rs.getDate("fecha_solicitud") != null) {
                        dto.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                    }
                    if (rs.getDate("fecha_prestamo") != null) {
                        dto.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                    }
                    if (rs.getDate("fecha_limite") != null) {
                        dto.setFechaLimite(rs.getDate("fecha_limite").toLocalDate());
                    }
                    if (rs.getDate("fecha_devolucion") != null) {
                        dto.setFechaDevolucion(rs.getDate("fecha_devolucion").toLocalDate());
                    }

                    dto.setEstado(EstadoPrestamo.valueOf(rs.getString("estado")));
                    dto.setRenovaciones(rs.getInt("renovaciones"));
                    dto.setMultaPagada(rs.getBoolean("multa_pagada"));
                    
                    double multaCalculada = calcularMultaDinamica(
                            dto.getFechaLimite(), 
                            rs.getDouble("multa_generada"), 
                            tarifaMora, 
                            rs.getString("estado")
                    );
                    dto.setMultaGenerada(multaCalculada);

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los préstamos: " + e.getMessage(), e);
        }
        return lista;
    }
    
    private double calcularMultaDinamica(LocalDate limite, double multaBD, double tarifa, String estado) {
        if (EstadoPrestamo.ACTIVO.name().equals(estado) && limite != null) {
            LocalDate hoy = LocalDate.now();
            if (hoy.isAfter(limite)) {
                long diasAtraso = ChronoUnit.DAYS.between(limite, hoy);
                return diasAtraso * tarifa;
            }
        }
        return multaBD; // Si no está activo o no hay atraso, devuelve lo que ya tiene la BD
    }

    public List<PrestamoResponseDTO> listarMultasPendientes() throws BDException {
        List<PrestamoResponseDTO> lista = new ArrayList<>();
        String query = "SELECT p.*, l.codigo AS codigo_libro, l.titulo AS titulo_libro, "
                + "e.nombres AS est_nom, e.apellidos AS est_ape, "
                + "emp.nombres AS emp_nom, emp.apellidos AS emp_ape "
                + "FROM prestamo p "
                + "INNER JOIN libro l ON p.id_libro = l.id_libro "
                + "LEFT JOIN estudiante e ON p.id_estudiante = e.id_estudiante "
                + "LEFT JOIN empleado emp ON p.id_empleado = emp.id_empleado "
                + "WHERE p.multa_generada > 0 AND p.multa_pagada = false "
                + "ORDER BY p.fecha_devolucion DESC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PrestamoResponseDTO dto = new PrestamoResponseDTO();
                dto.setIdPrestamo(rs.getInt("id_prestamo"));
                dto.setIdLibro(rs.getInt("id_libro"));
                dto.setTituloLibro(rs.getString("titulo_libro"));
                dto.setCodigoLibro(rs.getString("codigo_libro"));

                if (rs.getObject("id_estudiante") != null) {
                    dto.setNombreSolicitante(rs.getString("est_nom") + " " + rs.getString("est_ape"));
                    dto.setTipoSolicitante("ESTUDIANTE");
                    dto.setIdEstudiante(rs.getInt("id_estudiante"));
                } else {
                    dto.setNombreSolicitante(rs.getString("emp_nom") + " " + rs.getString("emp_ape"));
                    dto.setTipoSolicitante("MAESTRO");
                    dto.setIdEmpleado(rs.getInt("id_empleado"));
                }

                if (rs.getDate("fecha_solicitud") != null) {
                    dto.setFechaSolicitud(rs.getDate("fecha_solicitud").toLocalDate());
                }
                if (rs.getDate("fecha_prestamo") != null) {
                    dto.setFechaPrestamo(rs.getDate("fecha_prestamo").toLocalDate());
                }
                if (rs.getDate("fecha_limite") != null) {
                    dto.setFechaLimite(rs.getDate("fecha_limite").toLocalDate());
                }
                if (rs.getDate("fecha_devolucion") != null) {
                    dto.setFechaDevolucion(rs.getDate("fecha_devolucion").toLocalDate());
                }

                dto.setEstado(EstadoPrestamo.valueOf(rs.getString("estado")));
                dto.setRenovaciones(rs.getInt("renovaciones"));
                dto.setMultaGenerada(rs.getDouble("multa_generada"));
                dto.setMultaPagada(rs.getBoolean("multa_pagada"));

                lista.add(dto);
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar multas pendientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean pagarMulta(int idPrestamo, String numeroFactura, int idCiclo) throws BDException {
        String selectPrestamo = "SELECT id_estudiante, id_empleado, multa_generada FROM prestamo WHERE id_prestamo = ? AND multa_pagada = FALSE";
        String insertPago = "INSERT INTO pago (numero_factura, id_estudiante, id_empleado, id_ciclo, concepto, descripcion, monto, fecha_pago, estado, id_prestamo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updatePrestamo = "UPDATE prestamo SET multa_pagada = TRUE WHERE id_prestamo = ?";

        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getInstance().getConnection();
            conn.setAutoCommit(false);

            Integer idEstudiante = null;
            Integer idEmpleado = null;
            double montoMulta = 0.0;

            // extraer deudor y deuda
            try (PreparedStatement psSelect = conn.prepareStatement(selectPrestamo)) {
                psSelect.setInt(1, idPrestamo);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        idEstudiante = (Integer) rs.getObject("id_estudiante");
                        idEmpleado = (Integer) rs.getObject("id_empleado");
                        montoMulta = rs.getDouble("multa_generada");
                    } else {
                        throw new BDException("No se encontró la multa o ya estaba pagada.");
                    }
                }
            }

            // insertar pago
            try (PreparedStatement psInsert = conn.prepareStatement(insertPago)) {
                psInsert.setString(1, numeroFactura);
                psInsert.setObject(2, idEstudiante, java.sql.Types.INTEGER);
                psInsert.setObject(3, idEmpleado, java.sql.Types.INTEGER);
                psInsert.setInt(4, idCiclo);
                psInsert.setString(5, enums.ConceptoPago.MULTA_BIBLIOTECA.name());
                psInsert.setString(6, "Pago de multa por retraso de biblioteca");
                psInsert.setDouble(7, montoMulta);
                psInsert.setDate(8, java.sql.Date.valueOf(java.time.LocalDate.now()));
                psInsert.setString(9, enums.EstadoPago.ACTIVO.name());
                psInsert.setInt(10, idPrestamo);
                psInsert.executeUpdate();
            }

            // quitar mora
            try (PreparedStatement psUpdate = conn.prepareStatement(updatePrestamo)) {
                psUpdate.setInt(1, idPrestamo);
                psUpdate.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException | BDException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BDException("Error al registrar el pago. Es posible que el número de factura ya exista.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public double obtenerTarifaMora(Connection conn) {
        String query = "SELECT valor FROM configuracion_sistema WHERE clave = 'MORA_DIARIA_BIBLIOTECA'";
        try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("valor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 5.00; 
    }
}
