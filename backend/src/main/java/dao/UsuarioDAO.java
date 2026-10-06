package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Usuario;
import enums.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {

    private DBConection conexionDB = new DBConection();

    public Optional<Usuario> autenticar(String correo, String password) throws BDException {
        String query = "SELECT * FROM usuario WHERE correo = ?";
        Usuario usuario = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashBD = rs.getString("password");

                    //verifica si la contraseña coincide con el hash guardado
                    if (BCrypt.checkpw(password, hashBD)) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setPassword(hashBD);
                        usuario.setRol(Rol.valueOf(rs.getString("rol")));

                        //el endpoint decidirá que hacer si es false
                        usuario.setEstado(rs.getBoolean("estado"));

                        int idEmpleado = rs.getInt("id_empleado");
                        if (!rs.wasNull()) {
                            usuario.setIdEmpleado(idEmpleado);
                        }

                        int idEstudiante = rs.getInt("id_estudiante");
                        if (!rs.wasNull()) {
                            usuario.setIdEstudiante(idEstudiante);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new BDException("Error de base de datos al intentar autenticar al usuario: " + e.getMessage(), e);
        }
        return Optional.ofNullable(usuario);
    }

    public boolean registrarUsuario(Usuario usuario) throws BDException {
        String query = "INSERT INTO usuario (correo, password, rol, estado, id_empleado, id_estudiante) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, usuario.getCorreo());

            // hashea la contraseña antes de guardarla en la base de datos
            // gensalt() genera una "semilla" aleatoria para hacer el hash aun mas seguro
            String password = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
            ps.setString(2, password);
            ps.setString(3, usuario.getRol().name());
            ps.setBoolean(4, usuario.isEstado());

            if (usuario.getIdEmpleado() != null) {
                ps.setInt(5, usuario.getIdEmpleado());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            if (usuario.getIdEstudiante() != null) {
                ps.setInt(6, usuario.getIdEstudiante());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el usuario en la base de datos", e);
        }
    }

    public boolean modificarPerfil(Usuario usuario) throws BDException {
        String query = "UPDATE usuario SET correo = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, usuario.getCorreo());
            ps.setInt(2, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el perfil del usuario: " + e.getMessage(), e);
        }
    }

    public boolean actualizarPassword(int idUsuario, String nuevaContraseña) throws BDException {
        String query = "UPDATE usuario SET password = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            // hashea la nueva contraseña 
            String nuevoHash = BCrypt.hashpw(nuevaContraseña, BCrypt.gensalt());

            ps.setString(1, nuevoHash);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la contraseña del usuario en la base de datos: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> buscarPorCorreo(String correo) throws BDException {
        String query = "SELECT * FROM usuario WHERE correo = ?";
        Usuario usuario = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.next()) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setPassword(rs.getString("password"));
                        usuario.setRol(Rol.valueOf(rs.getString("rol")));
                        usuario.setEstado(rs.getBoolean("estado"));

                        int idEmpleado = rs.getInt("id_empleado");
                        if (!rs.wasNull()) {
                            usuario.setIdEmpleado(idEmpleado);
                        }

                        int idEstudiante = rs.getInt("id_estudiante");
                        if (!rs.wasNull()) {
                            usuario.setIdEstudiante(idEstudiante);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BDException("Error al buscar el usuario por correo: " + e.getMessage(), e);
        }

        return Optional.ofNullable(usuario);
    }

    //no actualiza contraseña ni estado
    public boolean modificarUsuario(Usuario usuario) throws BDException {
        String query = "UPDATE usuario SET correo = ?, rol = ?, id_empleado = ?, id_estudiante = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getRol().name());

            if (usuario.getIdEmpleado() != null) {
                ps.setInt(3, usuario.getIdEmpleado());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            if (usuario.getIdEstudiante() != null) {
                ps.setInt(4, usuario.getIdEstudiante());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setInt(5, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el usuario: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idUsuario, boolean estado) throws BDException {
        String query = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarPaginado(int pagina, int tamanoPagina) throws BDException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String query = "SELECT * FROM usuario LIMIT ? OFFSET ?";

        // punto de inicio 
        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tamanoPagina);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setRol(Rol.valueOf(rs.getString("rol")));
                    usuario.setEstado(rs.getBoolean("estado"));

                    int idEmpleado = rs.getInt("id_empleado");
                    if (!rs.wasNull()) {
                        usuario.setIdEmpleado(idEmpleado);
                    }

                    int idEstudiante = rs.getInt("id_estudiante");
                    if (!rs.wasNull()) {
                        usuario.setIdEstudiante(idEstudiante);
                    }

                    listaUsuarios.add(usuario);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los usuarios paginados: " + e.getMessage(), e);
        }

        return listaUsuarios;
    }
}
