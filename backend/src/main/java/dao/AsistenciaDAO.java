package dao;

import dbconection.DBConnectionSingleton;
import dto.AsistenciaRequestDTO;
import enums.EstadoAsistencia;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AsistenciaDAO {

    // registrar asistencia diaria
    public boolean registrarAsistencia(AsistenciaRequestDTO asistencia) throws BDException {
        String query = "INSERT INTO asistencia (id_estudiante, id_asignacion, fecha, estado) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, asistencia.getIdEstudiante());
            ps.setInt(2, asistencia.getIdAsignacion());
            ps.setDate(3, Date.valueOf(asistencia.getFecha()));
            EstadoAsistencia estadoEnum = EstadoAsistencia.valueOf(asistencia.getEstado().toUpperCase());
            ps.setString(4, estadoEnum.name());

            return ps.executeUpdate() > 0;

        } catch (IllegalArgumentException e) {
            throw new BDException("Estado de asistencia inválido. Debe ser PRESENTE, AUSENTE o JUSTIFICADO.", e);
        } catch (SQLException e) {
            throw new BDException("Error al registrar la asistencia en la base de datos: " + e.getMessage(), e);
        }
    }

    public boolean modificarAsistencia(int idAsistencia, EstadoAsistencia nuevoEstado) throws BDException {
        String query = "UPDATE asistencia SET estado = ? WHERE id_asistencia = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idAsistencia);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la asistencia: " + e.getMessage(), e);
        }
    }
}
