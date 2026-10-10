package dao;

import dbconection.DBConnectionSingleton;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Base64; 
import modelos.Empleado;
import modelos.Usuario;

public class EmpleadoDAO {

    public Optional<Empleado> buscarPorDpi(String dpi) throws BDException {
        String query = "SELECT * FROM empleado WHERE dpi = ?";
        Empleado empleado = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, dpi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = new Empleado();
                    empleado.setIdEmpleado(rs.getInt("id_empleado"));
                    empleado.setDpi(rs.getString("dpi"));
                    empleado.setNombres(rs.getString("nombres"));
                    empleado.setApellidos(rs.getString("apellidos"));
                    empleado.setPuesto(rs.getString("puesto"));
                    empleado.setFechaContratacion(rs.getObject("fecha_contratacion", LocalDate.class));
                    empleado.setSalario(rs.getDouble("salario"));
                    empleado.setEstado(rs.getBoolean("estado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el empleado por DPI: " + e.getMessage(), e);
        }

        return Optional.ofNullable(empleado);
    }

    public boolean registrarEmpleado(Empleado empleado) throws BDException {
        String query = "INSERT INTO empleado (dpi, nombres, apellidos, puesto, fecha_contratacion, salario, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, empleado.getDpi());
            ps.setString(2, empleado.getNombres());
            ps.setString(3, empleado.getApellidos());
            ps.setString(4, empleado.getPuesto());
            ps.setObject(5, empleado.getFechaContratacion());
            ps.setDouble(6, empleado.getSalario());
            ps.setBoolean(7, empleado.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el empleado: " + e.getMessage(), e);
        }
    }

    public boolean registrarEmpleadoConUsuario(Empleado empleado, Usuario usuario) throws BDException {
        String queryEmpleado = "INSERT INTO empleado (dpi, nombres, apellidos, puesto, fecha_contratacion, salario, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String queryUsuario = "INSERT INTO usuario (correo, password, rol, estado, id_empleado) VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;

        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);

            int idEmpleadoGenerado = 0;
            try (PreparedStatement psEmpleado = connection.prepareStatement(queryEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                psEmpleado.setString(1, empleado.getDpi());
                psEmpleado.setString(2, empleado.getNombres());
                psEmpleado.setString(3, empleado.getApellidos());
                psEmpleado.setString(4, empleado.getPuesto());
                psEmpleado.setObject(5, empleado.getFechaContratacion());
                psEmpleado.setDouble(6, empleado.getSalario());
                psEmpleado.setBoolean(7, empleado.isEstado());

                psEmpleado.executeUpdate();

                try (ResultSet rs = psEmpleado.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEmpleadoGenerado = rs.getInt(1);
                    }
                }
            }

            if (idEmpleadoGenerado > 0) {
                try (PreparedStatement psUsuario = connection.prepareStatement(queryUsuario)) {
                    psUsuario.setString(1, usuario.getCorreo());

                    // contraseña en base64 antes de guardarla
                    String passwordBase64 = Base64.getEncoder().encodeToString(usuario.getPassword().getBytes());
                    psUsuario.setString(2, passwordBase64);

                    psUsuario.setString(3, usuario.getRol().name());
                    psUsuario.setBoolean(4, usuario.isEstado());
                    psUsuario.setInt(5, idEmpleadoGenerado);

                    psUsuario.executeUpdate();
                }
            } else {
                connection.rollback();
                return false;
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
            throw new BDException("Error al registrar el empleado junto con su usuario: " + e.getMessage(), e);

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

    public boolean modificarEmpleado(Empleado empleado) throws BDException {
        String query = "UPDATE empleado SET nombres = ?, apellidos = ?, puesto = ?, fecha_contratacion = ?, salario = ? WHERE id_empleado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, empleado.getNombres());
            ps.setString(2, empleado.getApellidos());
            ps.setString(3, empleado.getPuesto());
            ps.setObject(4, empleado.getFechaContratacion());
            ps.setDouble(5, empleado.getSalario());
            ps.setInt(6, empleado.getIdEmpleado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el empleado: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idEmpleado, boolean estado) throws BDException {
        String query = "UPDATE empleado SET estado = ? WHERE id_empleado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idEmpleado);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del empleado: " + e.getMessage(), e);
        }
    }

    public List<Empleado> listarPaginado(int pagina, int tamanoPagina) throws BDException {
        List<Empleado> listaEmpleados = new ArrayList<>();
        String query = "SELECT * FROM empleado LIMIT ? OFFSET ?";

        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tamanoPagina);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Empleado empleado = new Empleado(
                            rs.getInt("id_empleado"),
                            rs.getString("dpi"),
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            rs.getString("puesto"),
                            rs.getObject("fecha_contratacion", LocalDate.class),
                            rs.getDouble("salario"),
                            rs.getBoolean("estado"));

                    listaEmpleados.add(empleado);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los empleados: " + e.getMessage(), e);
        }

        return listaEmpleados;
    }
}
