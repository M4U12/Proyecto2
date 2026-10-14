package dao;

import dbconection.DBConnectionSingleton;
import excepciones.BDException;
import modelos.Usuario;
import enums.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

public class UsuarioDAO {

    public Optional<Usuario> autenticar(String correo, String password) throws BDException {
        String query = "SELECT * FROM usuario WHERE correo = ?";
        Usuario usuario = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String pass = rs.getString("password");

                    String passwordCodificada = Base64.getEncoder().encodeToString(password.getBytes());

                    // verifica si la contraseña codificada coincide con la guardada
                    if (passwordCodificada.equals(pass)) {
                        usuario = new Usuario();
                        usuario.setIdUsuario(rs.getInt("id_usuario"));
                        usuario.setCorreo(rs.getString("correo"));
                        usuario.setPassword(pass);
                        usuario.setRol(Rol.valueOf(rs.getString("rol")));

                        // el endpoint decidirá qué hacer si es false
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

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, usuario.getCorreo());

            // usar base64 antes de guardarla
            String passwordCodificada = Base64.getEncoder().encodeToString(usuario.getPassword().getBytes());
            ps.setString(2, passwordCodificada);

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

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, usuario.getCorreo());
            ps.setInt(2, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el perfil del usuario: " + e.getMessage(), e);
        }
    }

    public boolean actualizarPassword(int idUsuario, String nuevaContrasena) throws BDException {
        String query = "UPDATE usuario SET password = ? WHERE id_usuario = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            // nueva contraseña en Base64
            String nuevaPassword = Base64.getEncoder().encodeToString(nuevaContrasena.getBytes());

            ps.setString(1, nuevaPassword);
            ps.setInt(2, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la contraseña del usuario en la base de datos: " + e.getMessage(), e);
        }
    }

    public boolean actualizarPasswordPorCorreo(String correo, String nuevaContrasena) throws BDException {
        String query = "UPDATE usuario SET password = ? WHERE correo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            String nuevaPasswordBase64 = Base64.getEncoder().encodeToString(nuevaContrasena.getBytes());
            ps.setString(1, nuevaPasswordBase64);
            ps.setString(2, correo);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al actualizar la contraseña: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> buscarPorCorreo(String correo) throws BDException {
        String query = "SELECT * FROM usuario WHERE correo = ?";
        Usuario usuario = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
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
        } catch (Exception e) {
            throw new BDException("Error al buscar el usuario por correo: " + e.getMessage(), e);
        }

        return Optional.ofNullable(usuario);
    }

    //no actualiza contraseña ni estado
    public boolean modificarUsuario(Usuario usuario) throws BDException {
        String query = "UPDATE usuario SET correo = ?, rol = ?, id_empleado = ?, id_estudiante = ? WHERE id_usuario = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

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
        String queryRol = "SELECT rol FROM usuario WHERE id_usuario = ?";
        String queryCount = "SELECT COUNT(*) FROM usuario WHERE rol = 'SUPER_ADMIN' AND estado = true";
        String queryUpdate = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {

            // para evitar desactivar al ultimo super admin
            if (!estado) {
                try (PreparedStatement psRol = connection.prepareStatement(queryRol)) {
                    psRol.setInt(1, idUsuario);
                    try (ResultSet rsRol = psRol.executeQuery()) {
                        if (rsRol.next()) {
                            String rol = rsRol.getString("rol");

                            if ("SUPER_ADMIN".equals(rol)) {
                                try (PreparedStatement psCount = connection.prepareStatement(queryCount); ResultSet rsCount = psCount.executeQuery()) {

                                    if (rsCount.next() && rsCount.getInt(1) <= 1) {
                                        throw new BDException("Operación denegada: No se puede desactivar al único Super Administrador activo. Debe haber al menos uno en el sistema.");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            try (PreparedStatement psUpdate = connection.prepareStatement(queryUpdate)) {
                psUpdate.setBoolean(1, estado);
                psUpdate.setInt(2, idUsuario);

                return psUpdate.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarPaginado(int pagina, int tamanoPagina) throws BDException {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String query = "SELECT * FROM usuario LIMIT ? OFFSET ?";

        // punto de inicio 
        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

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

    public Optional<String> buscarPasswordPorId(int idUsuario) throws BDException {
        String query = "SELECT password FROM usuario WHERE id_usuario = ?";
        String pass = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pass = rs.getString("password");
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la contraseña del usuario: " + e.getMessage(), e);
        }

        return Optional.ofNullable(pass);
    }
    
    public void cambiarEstadoPorEmpleado(int idEmpleado, boolean nuevoEstado) throws BDException {
        String query = "UPDATE usuario SET estado = ? WHERE id_empleado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new BDException("Error al cambiar estado de usuario asociado al empleado: " + e.getMessage(), e);
        }
    }
    
    public void cambiarEstadoPorEstudiante(int idEstudiante, boolean nuevoEstado) throws BDException {
        String query = "UPDATE usuario SET estado = ? WHERE id_estudiante = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idEstudiante);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new BDException("Error al cambiar estado de usuario asociado al estudiante: " + e.getMessage(), e);
        }
    }
}
