package dao;

import dbconection.DBConnectionSingleton;
import dto.NotaRequestDTO;
import excepciones.BDException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NotaDAO {

    public boolean registrarNota(NotaRequestDTO nota) throws BDException {
        String query = "INSERT INTO nota (id_actividad, id_estudiante, punteo_obtenido) VALUES (?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, nota.getIdActividad());
            ps.setInt(2, nota.getIdEstudiante());
            ps.setDouble(3, nota.getPunteoObtenido());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la nota: " + e.getMessage(), e);
        }
    }

    public boolean modificarNota(int idNota, double nuevoPunteo) throws BDException {
        String query = "UPDATE nota SET punteo_obtenido = ? WHERE id_nota = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setDouble(1, nuevoPunteo);
            ps.setInt(2, idNota);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la nota: " + e.getMessage(), e);
        }
    }

    public double consultarZonaTotal(int idEstudiante, int idAsignacion) throws BDException {
        String query = "SELECT SUM(n.punteo_obtenido) AS zona_total "
                + "FROM nota n "
                + "INNER JOIN actividad a ON n.id_actividad = a.id_actividad "
                + "WHERE n.id_estudiante = ? AND a.id_asignacion = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double zona = rs.getDouble("zona_total");
                    return rs.wasNull() ? 0.0 : zona;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al calcular la zona del estudiante: " + e.getMessage(), e);
        }
        return 0.0;
    }
}
