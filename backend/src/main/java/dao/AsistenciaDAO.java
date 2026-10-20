package dao;

import dbconection.DBConnectionSingleton;
import dto.AsistenciaRequestDTO;
import dto.AsistenciaResponseDTO;
import enums.EstadoAsistencia;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    
    public List<AsistenciaResponseDTO> consultarAsistenciaPorFecha(int idAsignacion, LocalDate fecha) throws BDException {
        List<AsistenciaResponseDTO> lista = new ArrayList<>();
        
        String query = "SELECT a.id_asistencia, e.id_estudiante, e.nombres, e.apellidos, a.fecha, a.estado " +
                       "FROM asistencia a " +
                       "INNER JOIN estudiante e ON a.id_estudiante = e.id_estudiante " +
                       "WHERE a.id_asignacion = ? AND a.fecha = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idAsignacion);
            ps.setDate(2, Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsistenciaResponseDTO dto = new AsistenciaResponseDTO();
                    dto.setIdAsistencia(rs.getInt("id_asistencia"));
                    dto.setIdEstudiante(rs.getInt("id_estudiante"));
                    dto.setNombres(rs.getString("nombres"));
                    dto.setApellidos(rs.getString("apellidos"));
                    dto.setFecha(rs.getDate("fecha").toLocalDate());
                    dto.setEstado(rs.getString("estado"));
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar asistencias: " + e.getMessage(), e);
        }
        return lista;
    }
}
