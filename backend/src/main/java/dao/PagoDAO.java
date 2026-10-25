package dao;

import dbconection.DBConnectionSingleton;
import dto.DetalleMesDTO;
import dto.EstadoCuentaDTO;
import dto.OtroPagoDTO;
import dto.PagoRequestDTO;
import dto.PagoResponseDTO;
import enums.ConceptoPago;
import enums.EstadoPago;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagoDAO {

    public boolean verificarInscripcionPagada(int idEstudiante, int idCiclo) {
        String query = "SELECT 1 FROM pago " +
                       "WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = 'INSCRIPCION' AND estado = 'ACTIVO' LIMIT 1";
        
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verificarColegiaturaPagada(int idEstudiante, int idCiclo, int mes) throws BDException {
        String query = "SELECT COUNT(*) AS total FROM pago WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = ? AND mes_colegiatura = ? AND estado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            ps.setString(3, enums.ConceptoPago.COLEGIATURA.name());
            ps.setInt(4, mes);
            ps.setString(5, enums.EstadoPago.ACTIVO.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el estado de la colegiatura: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean registrarPago(PagoRequestDTO pago) throws BDException {
        String query = "INSERT INTO pago (numero_factura, id_estudiante, id_ciclo, concepto, descripcion, monto, fecha_pago, estado, mes_colegiatura) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, pago.getNumeroFactura());
            ps.setInt(2, pago.getIdEstudiante());
            ps.setInt(3, pago.getIdCiclo());
            ps.setString(4, pago.getConcepto().name());
            ps.setString(5, pago.getDescripcion());
            ps.setDouble(6, pago.getMonto());
            ps.setDate(7, Date.valueOf(LocalDate.now()));
            ps.setString(8, enums.EstadoPago.ACTIVO.name());

            if (pago.getMesColegiatura() != null && pago.getMesColegiatura() > 0) {
                ps.setInt(9, pago.getMesColegiatura());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new BDException("El número de factura '" + pago.getNumeroFactura() + "' ya está registrado en el sistema.");
            }
            throw new BDException("Error al registrar el pago: " + e.getMessage(), e);
        }
    }

    public boolean anularPago(String numeroFactura, String motivoAnulacion) throws BDException {
        String selectPago = "SELECT id_pago, concepto, id_prestamo, estado FROM pago WHERE numero_factura = ?";
        String updatePago = "UPDATE pago SET estado = ?, motivo_anulacion = ? WHERE numero_factura = ?";
        String revertirMulta = "UPDATE prestamo SET multa_pagada = FALSE WHERE id_prestamo = ?";

        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getInstance().getConnection();
            conn.setAutoCommit(false);

            String concepto = null;
            Integer idPrestamo = null;

            // buscar la factura y verificar su estado
            try (PreparedStatement psSelect = conn.prepareStatement(selectPago)) {
                psSelect.setString(1, numeroFactura);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        if (enums.EstadoPago.ANULADO.name().equals(rs.getString("estado"))) {
                            throw new BDException("La factura ya se encuentra anulada.");
                        }
                        concepto = rs.getString("concepto");
                        idPrestamo = (Integer) rs.getObject("id_prestamo");
                    } else {
                        throw new BDException("No se encontró ninguna factura con ese número.");
                    }
                }
            }

            // marcar el pago como anulado
            try (PreparedStatement psUpdate = conn.prepareStatement(updatePago)) {
                psUpdate.setString(1, enums.EstadoPago.ANULADO.name());
                psUpdate.setString(2, motivoAnulacion);
                psUpdate.setString(3, numeroFactura);
                psUpdate.executeUpdate();
            }

            // si era multa de biblioteca, reactivar la deuda
            if (enums.ConceptoPago.MULTA_BIBLIOTECA.name().equals(concepto) && idPrestamo != null) {
                try (PreparedStatement psRevert = conn.prepareStatement(revertirMulta)) {
                    psRevert.setInt(1, idPrestamo);
                    psRevert.executeUpdate();
                }
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
            throw new BDException("Error al anular la factura: " + e.getMessage(), e);
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

    public List<PagoResponseDTO> listarPagos(int pagina, int limite) throws BDException {
        List<PagoResponseDTO> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;

        String query = "SELECT p.id_pago, p.numero_factura, p.concepto, p.descripcion, p.monto, p.fecha_pago, p.estado, "
                + "e.nombres AS est_nom, e.apellidos AS est_ape, "
                + "emp.nombres AS emp_nom, emp.apellidos AS emp_ape "
                + "FROM pago p "
                + "LEFT JOIN estudiante e ON p.id_estudiante = e.id_estudiante "
                + "LEFT JOIN empleado emp ON p.id_empleado = emp.id_empleado "
                + "ORDER BY p.fecha_pago DESC, p.id_pago DESC LIMIT ? OFFSET ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dto.PagoResponseDTO dto = new dto.PagoResponseDTO();
                    dto.setIdPago(rs.getInt("id_pago"));
                    dto.setNumeroFactura(rs.getString("numero_factura"));
                    dto.setConcepto(rs.getString("concepto"));
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setMonto(rs.getDouble("monto"));

                    if (rs.getDate("fecha_pago") != null) {
                        dto.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                    }

                    dto.setEstado(rs.getString("estado"));

                    // Determinar si fue pagado por un estudiante o un empleado
                    if (rs.getString("est_nom") != null) {
                        dto.setNombreDeudor(rs.getString("est_nom") + " " + rs.getString("est_ape"));
                        dto.setTipoDeudor("ESTUDIANTE");
                    } else {
                        dto.setNombreDeudor(rs.getString("emp_nom") + " " + rs.getString("emp_ape"));
                        dto.setTipoDeudor("MAESTRO");
                    }

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar el historial de pagos: " + e.getMessage(), e);
        }
        return lista;
    }

    public String verificarSolvenciaEstudiante(int idEstudiante, int idCiclo) throws BDException {
        if (!verificarInscripcionPagada(idEstudiante, idCiclo)) {
            return "No ha pagado la inscripción del ciclo actual.";
        }

        // Calcula hasta qué mes se le debe exigir el pago
        int mesActual = LocalDate.now().getMonthValue();
        int mesExigible = Math.min(mesActual, 10);

        //Obtener el último mes de colegiatura pagado
        String query = "SELECT MAX(mes_colegiatura) as ultimo_mes FROM pago "
                + "WHERE id_estudiante = ? AND id_ciclo = ? "
                + "AND concepto = 'COLEGIATURA' AND estado = 'ACTIVO'";

        try (Connection conn = dbconection.DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                int ultimoMesPagado = 0;
                if (rs.next()) {
                    ultimoMesPagado = rs.getInt("ultimo_mes"); // retorna 0 si es null
                }

                if (ultimoMesPagado < mesExigible) {
                    // calcula cual es el mes que le toca pagar
                    int mesDeuda = ultimoMesPagado + 1;
                    String[] nombresMeses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre"};
                    return "Debe la colegiatura del mes de " + nombresMeses[mesDeuda] + ".";
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar solvencia académica: " + e.getMessage(), e);
        }

        // Si superó ambas validaciones, no debe colegiatura ni inscripción
        return "";
    }

    public EstadoCuentaDTO obtenerEstadoCuenta(int idEstudiante, int idCiclo) throws BDException {
        EstadoCuentaDTO estadoCuenta = new EstadoCuentaDTO();
        estadoCuenta.setInscripcionPagada(verificarInscripcionPagada(idEstudiante, idCiclo));

        String[] nombresMeses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre"};
        List<DetalleMesDTO> listaMeses = new ArrayList<>();

        int mesActual = LocalDate.now().getMonthValue();

        String query = "SELECT mes_colegiatura, monto, fecha_pago, numero_factura FROM pago "
                + "WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = 'COLEGIATURA' AND estado = 'ACTIVO'";

        try (Connection conn = dbconection.DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                //se guardan los meses pagados en un mapa para acceso rápido
                Map<Integer, DetalleMesDTO> pagosRealizados = new HashMap<>();
                while (rs.next()) {
                    DetalleMesDTO detalle = new DetalleMesDTO();
                    detalle.setMonto(rs.getDouble("monto"));
                    detalle.setFechaPago(rs.getDate("fecha_pago").toString());
                    detalle.setNumeroFactura(rs.getString("numero_factura"));
                    pagosRealizados.put(rs.getInt("mes_colegiatura"), detalle);
                }

                // genera los 10 meses obligatorios
                for (int i = 1; i <= 10; i++) {
                    DetalleMesDTO mes = new DetalleMesDTO();
                    mes.setMesId(i);
                    mes.setNombreMes(nombresMeses[i]);

                    if (pagosRealizados.containsKey(i)) {
                        DetalleMesDTO pago = pagosRealizados.get(i);
                        mes.setEstado("PAGADO");
                        mes.setMonto(pago.getMonto());
                        mes.setFechaPago(pago.getFechaPago());
                        mes.setNumeroFactura(pago.getNumeroFactura());
                    } else {
                        // si el mes exigible ya pasó y no está pagado, está atrasado
                        mes.setEstado(i < mesActual ? "ATRASADO" : "PENDIENTE");
                        mes.setMonto(0.0);
                        mes.setFechaPago(null);
                        mes.setNumeroFactura(null);
                    }
                    listaMeses.add(mes);
                }
                estadoCuenta.setColegiaturas(listaMeses);
            }
            
            estadoCuenta.setOtrosPagos(obtenerOtrosPagos(conn, idEstudiante, idCiclo));
        } catch (SQLException e) {
            throw new BDException("Error al consultar el estado de cuenta: " + e.getMessage(), e);
        }
        return estadoCuenta;
    }
    
    private List<OtroPagoDTO> obtenerOtrosPagos(Connection conn, int idEstudiante, int idCiclo) {
        List<OtroPagoDTO> listaOtros = new ArrayList<>();
        String query = "SELECT concepto, descripcion, monto, fecha_pago, numero_factura FROM pago " +
                       "WHERE id_estudiante = ? AND id_ciclo = ? AND concepto IN ('EXTRACURRICULAR', 'MULTA_BIBLIOTECA') AND estado = 'ACTIVO'";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
             
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OtroPagoDTO otro = new OtroPagoDTO();
                    otro.setConcepto(rs.getString("concepto"));
                    otro.setDescripcion(rs.getString("descripcion"));
                    otro.setMonto(rs.getDouble("monto")); 
                    
                    Date fecha = rs.getDate("fecha_pago");
                    otro.setFechaPago(fecha != null ? fecha.toString() : null);
                    otro.setNumeroFactura(rs.getString("numero_factura"));
                    
                    listaOtros.add(otro);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener otros pagos: " + e.getMessage());
        }
        
        return listaOtros;
    }
}
