package dao;

import dbconection.DBConnectionSingleton;
import dto.AsignacionRequestDTO;
import dto.AsignacionResponseDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionMaestroDAO {

    public boolean registrarAsignacion(AsignacionRequestDTO asignacion) throws BDException {
        String query = "INSERT INTO asignacion_maestro (id_empleado, id_curso, id_seccion, id_ciclo) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, asignacion.getIdEmpleado());
            ps.setInt(2, asignacion.getIdCurso());
            ps.setInt(3, asignacion.getIdSeccion());
            ps.setInt(4, asignacion.getIdCiclo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la asignación docente (Verifica que no estes asignando a un curso/seccion con maestro): " + e.getMessage(), e);
        }
    }

    public List<AsignacionResponseDTO> consultarAsignacionesPorMaestro(int idEmpleado, int idCiclo) throws BDException {
        List<AsignacionResponseDTO> lista = new ArrayList<>();

        String query = "SELECT am.id_asignacion, CONCAT(e.nombres, ' ', e.apellidos) AS maestro, "
                + "c.nombre AS curso, g.nombre AS grado, s.identificador AS seccion, cl.anio "
                + "FROM asignacion_maestro am "
                + "INNER JOIN empleado e ON am.id_empleado = e.id_empleado "
                + "INNER JOIN curso c ON am.id_curso = c.id_curso "
                + "INNER JOIN seccion s ON am.id_seccion = s.id_seccion "
                + "INNER JOIN grado g ON s.id_grado = g.id_grado "
                + "INNER JOIN ciclo_lectivo cl ON am.id_ciclo = cl.id_ciclo "
                + "WHERE am.id_empleado = ? AND am.id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idEmpleado);
            ps.setInt(2, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsignacionResponseDTO dto = new AsignacionResponseDTO();
                    dto.setIdAsignacion(rs.getInt("id_asignacion"));
                    dto.setNombreMaestro(rs.getString("maestro"));
                    dto.setNombreCurso(rs.getString("curso"));
                    dto.setGrado(rs.getString("grado"));
                    dto.setSeccion(rs.getString("seccion"));
                    dto.setAnioCiclo(rs.getInt("anio"));

                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar las asignaciones del maestro: " + e.getMessage(), e);
        }

        return lista;
    }
}
