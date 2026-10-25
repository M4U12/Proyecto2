package dao;

import dbconection.DBConnectionSingleton;
import dto.PagoRequestDTO;
import enums.ConceptoPago;
import enums.EstadoPago;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PagoDAO {

    public boolean verificarInscripcionPagada(int idEstudiante, int idCiclo) throws BDException {
        String query = "SELECT COUNT(*) AS total FROM pago WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = ? AND estado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idCiclo);
            ps.setString(3, ConceptoPago.INSCRIPCION.name());
            ps.setString(4, EstadoPago.ACTIVO.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el estado de la inscripción: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean verificarColegiaturaPagada(int idEstudiante, int idCiclo, int mes) throws BDException {
        String query = "SELECT COUNT(*) AS total FROM pago WHERE id_estudiante = ? AND id_ciclo = ? AND concepto = ? AND mes_colegiatura = ? AND estado = ?";
        
        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
             
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
        String query = "INSERT INTO pago (numero_factura, id_estudiante, id_ciclo, concepto, descripcion, monto, fecha_pago, estado, mes_colegiatura) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, pago.getNumeroFactura());
            ps.setInt(2, pago.getIdEstudiante());
            ps.setInt(3, pago.getIdCiclo());
            ps.setString(4, pago.getConcepto().name()); 
            ps.setString(5, pago.getDescripcion());
            ps.setDouble(6, pago.getMonto());
            ps.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.now())); 
            ps.setString(8, enums.EstadoPago.ACTIVO.name()); 

            if (pago.getMesColegiatura() != null && pago.getMesColegiatura() > 0) {
                ps.setInt(9, pago.getMesColegiatura());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
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
}
