package dao;

import dbconection.DBConnectionSingleton;
import dto.EstudianteRequestDTO;
import dto.ExpedienteCompletoDTO;
import enums.EstadoEstudiante;
import excepciones.BDException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

public class EstudianteDAO {

    public boolean registrarEstudiante(EstudianteRequestDTO estudiante) throws BDException {
        String query = "INSERT INTO estudiante (codigo_personal, nombres, apellidos, fecha_nacimiento, informacion_medica, nombre_encargado, contacto_encargado, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, estudiante.getCodigoPersonal());
            ps.setString(2, estudiante.getNombres());
            ps.setString(3, estudiante.getApellidos());
            ps.setDate(4, Date.valueOf(estudiante.getFechaNacimiento()));
            ps.setString(5, estudiante.getInformacionMedica());
            ps.setString(6, estudiante.getNombreEncargado());
            ps.setString(7, estudiante.getContactoEncargado());
            ps.setString(8, EstadoEstudiante.ACTIVO.name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el estudiante: " + e.getMessage(), e);
        }
    }

    public boolean modificarEstudiante(String codigoPersonal, EstudianteRequestDTO estudiante) throws BDException {
        String query = "UPDATE estudiante SET nombres = ?, apellidos = ?, fecha_nacimiento = ?, "
                + "informacion_medica = ?, nombre_encargado = ?, contacto_encargado = ? "
                + "WHERE codigo_personal = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, estudiante.getNombres());
            ps.setString(2, estudiante.getApellidos());
            ps.setDate(3, Date.valueOf(estudiante.getFechaNacimiento()));
            ps.setString(4, estudiante.getInformacionMedica());
            ps.setString(5, estudiante.getNombreEncargado());
            ps.setString(6, estudiante.getContactoEncargado());
            ps.setString(7, codigoPersonal);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el estudiante: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstadoEstudiante(String codigoPersonal, EstadoEstudiante nuevoEstado) throws BDException {
        String query = "UPDATE estudiante SET estado = ? WHERE codigo_personal = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, nuevoEstado.name());
            ps.setString(2, codigoPersonal);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del estudiante: " + e.getMessage(), e);
        }
    }


    public Optional<ExpedienteCompletoDTO> consultarExpediente(String codigoPersonal) throws BDException {
        String query = "SELECT e.codigo_personal, e.nombres, e.apellidos, "
                + "e.fecha_nacimiento, e.informacion_medica, e.contacto_encargado, e.estado, "
                + "g.nombre AS grado, s.identificador AS seccion, c.anio AS ciclo "
                + "FROM estudiante e "
                + "LEFT JOIN inscripcion i ON e.id_estudiante = i.id_estudiante "
                + "LEFT JOIN grado g ON i.id_grado = g.id_grado "
                + "LEFT JOIN seccion s ON i.id_seccion = s.id_seccion "
                + "LEFT JOIN ciclo_lectivo c ON i.id_ciclo = c.id_ciclo "
                + "WHERE e.codigo_personal = ? "
                + "ORDER BY c.anio DESC LIMIT 1";

        ExpedienteCompletoDTO expediente = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, codigoPersonal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    expediente = new ExpedienteCompletoDTO();
                    expediente.setCodigoPersonal(rs.getString("codigo_personal"));
                    expediente.setNombres(rs.getString("nombres"));
                    expediente.setApellidos(rs.getString("apellidos"));

                    expediente.setInformacionMedica(rs.getString("informacion_medica"));
                    expediente.setContactoEncargado(rs.getString("contacto_encargado"));

                    String estadoDb = rs.getString("estado");
                    if (estadoDb != null) {
                        expediente.setEstadoAlumno(EstadoEstudiante.valueOf(estadoDb));
                    }

                    expediente.setNombreGradoActual(rs.getString("grado") != null ? rs.getString("grado") : "Sin asignación");
                    expediente.setIdentificadorSeccion(rs.getString("seccion") != null ? rs.getString("seccion") : "N/A");

                    int anioCiclo = rs.getInt("ciclo");
                    if (!rs.wasNull()) {
                        expediente.setAnioCicloActivo(anioCiclo);
                    }

                    // calculo de la edad 
                    LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento").toLocalDate();
                    int edadCalculada = Period.between(fechaNacimiento, LocalDate.now()).getYears();
                    expediente.setEdad(edadCalculada);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar el expediente del estudiante: " + e.getMessage(), e);
        }

        return Optional.ofNullable(expediente);
    }
}
