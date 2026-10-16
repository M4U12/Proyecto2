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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.Estudiante;

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
        String updateEstudiante = "UPDATE estudiante SET estado = ? WHERE codigo_personal = ?";
        String updateUsuario = "UPDATE usuario SET estado = ? WHERE id_estudiante = (SELECT id_estudiante FROM estudiante WHERE codigo_personal = ?)";

        Connection connection = null;

        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);

            // actualizar expediente
            try (PreparedStatement psEst = connection.prepareStatement(updateEstudiante)) {
                psEst.setString(1, nuevoEstado.name());
                psEst.setString(2, codigoPersonal);

                if (psEst.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            //actualizar cuenta de usuario
            try (PreparedStatement psUsu = connection.prepareStatement(updateUsuario)) {
                // Si el alumno pasa a INACTIVO, su acceso es false. Si es ACTIVO o GRADUADO, su acceso es true.
                boolean estadoCuenta = (nuevoEstado != EstadoEstudiante.INACTIVO);
                psUsu.setBoolean(1, estadoCuenta);
                psUsu.setString(2, codigoPersonal);
                psUsu.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BDException("Error en transacción al cambiar el estado del estudiante: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Optional<ExpedienteCompletoDTO> consultarExpediente(String codigoPersonal) throws BDException {
        String query = "SELECT e.id_estudiante, e.codigo_personal, e.nombres, e.apellidos, "
                + "e.fecha_nacimiento, e.informacion_medica, e.nombre_encargado, e.contacto_encargado, e.estado, "
                + "g.id_grado AS id_grado_actual, g.nombre AS grado, s.identificador AS seccion, "
                + "c.id_ciclo, c.anio AS ciclo "
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
                    expediente.setIdEstudiante(rs.getInt("id_estudiante"));
                    expediente.setCodigoPersonal(rs.getString("codigo_personal"));
                    expediente.setNombres(rs.getString("nombres"));
                    expediente.setApellidos(rs.getString("apellidos"));
                    expediente.setInformacionMedica(rs.getString("informacion_medica"));
                    expediente.setNombreEncargado(rs.getString("nombre_encargado"));
                    expediente.setContactoEncargado(rs.getString("contacto_encargado"));
              
                    String estadoDb = rs.getString("estado");
                    if (estadoDb != null) {
                        expediente.setEstadoAlumno(EstadoEstudiante.valueOf(estadoDb));
                    }
                    
                    int idGrado = rs.getInt("id_grado_actual");
                    if (!rs.wasNull()) {
                        expediente.setIdGradoActual(idGrado);
                    }

                    expediente.setNombreGradoActual(rs.getString("grado") != null ? rs.getString("grado") : "Sin asignación");
                    expediente.setIdentificadorSeccion(rs.getString("seccion") != null ? rs.getString("seccion") : "N/A");

                    int anioCiclo = rs.getInt("ciclo");
                    if (!rs.wasNull()) {
                        expediente.setAnioCicloActivo(anioCiclo);
                    }
                    
                    int idCiclo = rs.getInt("id_ciclo");
                    if (!rs.wasNull()) {
                        expediente.setIdCicloActivo(idCiclo);
                    }
                    
                    LocalDate fechaNac = rs.getDate("fecha_nacimiento").toLocalDate();
                    expediente.setFechaNacimiento(fechaNac);

                    // calculo de la edad 
                    int edadCalculada = Period.between(fechaNac, LocalDate.now()).getYears();
                    expediente.setEdad(edadCalculada);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar el expediente del estudiante: " + e.getMessage(), e);
        }

        return Optional.ofNullable(expediente);
    }

    // solo devuelve el código y nombre
    public List<Estudiante> buscarCodigosPorNombre(String filtro) throws BDException {
        List<Estudiante> lista = new ArrayList<>();

        // busqueda flexible por nombre o apellido
        String query = "SELECT codigo_personal, nombres, apellidos FROM estudiante "
                + "WHERE nombres LIKE ? OR apellidos LIKE ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            String comodin = "%" + filtro.trim() + "%";
            ps.setString(1, comodin);
            ps.setString(2, comodin);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modelos.Estudiante e = new modelos.Estudiante();
                    e.setCodigoPersonal(rs.getString("codigo_personal"));
                    e.setNombres(rs.getString("nombres"));
                    e.setApellidos(rs.getString("apellidos"));
                    lista.add(e);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar códigos por nombre: " + e.getMessage(), e);
        }

        return lista;
    }
}
