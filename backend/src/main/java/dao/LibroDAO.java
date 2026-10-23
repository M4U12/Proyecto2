package dao;

import dbconection.DBConnectionSingleton;
import dto.LibroRequestDTO;
import excepciones.BDException;
import modelos.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public boolean registrarLibro(LibroRequestDTO request) throws BDException {
        String query = "INSERT INTO libro (codigo, titulo, autor, editorial, copias_totales, copias_disponibles, imagen, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, request.getCodigo());
            ps.setString(2, request.getTitulo());
            ps.setString(3, request.getAutor());
            ps.setString(4, request.getEditorial());
            ps.setInt(5, request.getCopiasTotales());
            ps.setInt(6, request.getCopiasTotales());
            ps.setString(7, request.getImagen());
            ps.setString(8, "ACTIVO");

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el libro (¿Código duplicado?): " + e.getMessage(), e);
        }
    }

    public boolean modificarLibro(int idLibro, LibroRequestDTO request) throws BDException {
        // obtener el libro actual para saber cuantas copias estaban prestadas
        String selectQuery = "SELECT copias_totales, copias_disponibles FROM libro WHERE id_libro = ?";
        String updateQuery = "UPDATE libro SET codigo=?, titulo=?, autor=?, editorial=?, copias_totales=?, copias_disponibles=?, imagen=? WHERE id_libro=?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {
            connection.setAutoCommit(false);

            int prestados = 0;
            try (PreparedStatement psSelect = connection.prepareStatement(selectQuery)) {
                psSelect.setInt(1, idLibro);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        prestados = rs.getInt("copias_totales") - rs.getInt("copias_disponibles");
                    } else {
                        return false; // el libro no existe
                    }
                }
            }

            if (request.getCopiasTotales() < prestados) {
                throw new BDException("No puedes reducir las copias totales a un número menor de los que ya están prestados (" + prestados + ").");
            }

            int nuevasCopiasDisponibles = request.getCopiasTotales() - prestados;

            try (PreparedStatement psUpdate = connection.prepareStatement(updateQuery)) {
                psUpdate.setString(1, request.getCodigo());
                psUpdate.setString(2, request.getTitulo());
                psUpdate.setString(3, request.getAutor());
                psUpdate.setString(4, request.getEditorial());
                psUpdate.setInt(5, request.getCopiasTotales());
                psUpdate.setInt(6, nuevasCopiasDisponibles);
                psUpdate.setString(7, request.getImagen());
                psUpdate.setInt(8, idLibro);

                psUpdate.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el libro: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idLibro, boolean nuevoEstado) throws BDException {
        String querySelect = "SELECT copias_totales, copias_disponibles FROM libro WHERE id_libro = ?";
        String queryUpdate = "UPDATE libro SET estado = ? WHERE id_libro = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {

            // si se va a desactivar, valida que no haya libros prestados
            if (!nuevoEstado) {
                try (PreparedStatement psSelect = connection.prepareStatement(querySelect)) {
                    psSelect.setInt(1, idLibro);
                    try (ResultSet rs = psSelect.executeQuery()) {
                        if (rs.next()) {
                            int totales = rs.getInt("copias_totales");
                            int disponibles = rs.getInt("copias_disponibles");
                            if (totales != disponibles) {
                                throw new BDException("Operación denegada: No se puede desactivar el libro. Hay " + (totales - disponibles) + " copia(s) en préstamo.");
                            }
                        }
                    }
                }
            }

            try (PreparedStatement psUpdate = connection.prepareStatement(queryUpdate)) {
                psUpdate.setString(1, nuevoEstado ? "ACTIVO" : "INACTIVO");
                psUpdate.setInt(2, idLibro);
                return psUpdate.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del libro: " + e.getMessage(), e);
        }
    }

    public List<Libro> listarLibros(int pagina, int limite, String filtro) throws BDException {
        List<Libro> lista = new ArrayList<>();
        int offset = (pagina - 1) * limite;
        boolean tieneFiltro = filtro != null && !filtro.trim().isEmpty();

        String query;
        if (tieneFiltro) {
            query = "SELECT * FROM libro WHERE codigo LIKE ? OR titulo LIKE ? OR autor LIKE ? LIMIT ? OFFSET ?";
        } else {
            query = "SELECT * FROM libro ORDER BY id_libro DESC LIMIT ? OFFSET ?";
        }

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            if (tieneFiltro) {
                String comodin = "%" + filtro.trim() + "%";
                ps.setString(1, comodin);
                ps.setString(2, comodin);
                ps.setString(3, comodin);
                ps.setInt(4, limite);
                ps.setInt(5, offset);
            } else {
                ps.setInt(1, limite);
                ps.setInt(2, offset);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro(
                    rs.getInt("id_libro"),
                    rs.getString("codigo"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editorial"),
                    rs.getInt("copias_totales"),
                    rs.getInt("copias_disponibles"),
                    rs.getString("imagen"),
                    "ACTIVO".equals(rs.getString("estado")));
                    lista.add(libro);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los libros: " + e.getMessage(), e);
        }

        return lista;
    }
}
