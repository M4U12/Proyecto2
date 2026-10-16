package dao;

import dbconection.DBConnectionSingleton;
import dto.InscripcionRequestDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class InscripcionDAO {

    public boolean inscribirEstudiante(InscripcionRequestDTO inscripcion) throws BDException {
        String query = "INSERT INTO inscripcion (id_estudiante, id_grado, id_seccion, id_ciclo, fecha_inscripcion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdGrado());
            ps.setInt(3, inscripcion.getIdSeccion());
            ps.setInt(4, inscripcion.getIdCiclo());
            ps.setDate(5, Date.valueOf(LocalDate.now()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la inscripción: " + e.getMessage(), e);
        }
    }

    public boolean hayCupoDisponible(int idSeccion) throws BDException {
        String query = "SELECT s.cupo, (SELECT COUNT(*) FROM inscripcion i WHERE i.id_seccion = s.id_seccion) AS inscritos_actuales "
                + "FROM seccion s WHERE s.id_seccion = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cupoMaximo = rs.getInt("cupo");
                    int inscritosActuales = rs.getInt("inscritos_actuales");

                    return inscritosActuales < cupoMaximo;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el cupo de la sección: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean corregirInscripcion(int idEstudiante, int idCiclo, int idNuevoGrado, int idNuevaSeccion) throws BDException {
        String query = "UPDATE inscripcion SET id_grado = ?, id_seccion = ? WHERE id_estudiante = ? AND id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idNuevoGrado);
            ps.setInt(2, idNuevaSeccion);
            ps.setInt(3, idEstudiante);
            ps.setInt(4, idCiclo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al corregir la inscripción del estudiante: " + e.getMessage(), e);
        }
    }

    public int contarInscritosPorCarrera(int idCarrera) throws BDException {
        String query = "SELECT COUNT(*) AS total_inscritos "
                + "FROM inscripcion i "
                + "INNER JOIN grado g ON i.id_grado = g.id_grado "
                + "WHERE g.id_carrera = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idCarrera);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_inscritos");
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al contar las inscripciones de la carrera: " + e.getMessage(), e);
        }
        return 0;
    }

    public int contarInscritosPorSeccion(int idSeccion) throws BDException {
        String query = "SELECT COUNT(*) AS total_inscritos FROM inscripcion WHERE id_seccion = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_inscritos");
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al contar las inscripciones de la sección: " + e.getMessage(), e);
        }
        return 0;
    }
}
