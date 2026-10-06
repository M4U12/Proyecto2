package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Carrera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarreraDAO {

    private DBConection conexionDB = new DBConection();

    public Optional<Carrera> buscarPorCodigo(String codigo) throws BDException {
        String query = "SELECT * FROM carrera WHERE codigo = ?";
        Carrera carrera = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carrera = new Carrera(
                    rs.getInt("id_carrera"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getInt("duracion_anios"),
                    rs.getBoolean("estado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la carrera por código: " + e.getMessage(), e);
        }

        return Optional.ofNullable(carrera);
    }


    public boolean crearCarrera(Carrera carrera) throws BDException {
        String query = "INSERT INTO carrera (codigo, nombre, duracion_anios, estado) VALUES (?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, carrera.getCodigo());
            ps.setString(2, carrera.getNombre());
            ps.setInt(3, carrera.getDuracionAnios());
            ps.setBoolean(4, carrera.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la carrera: " + e.getMessage(), e);
        }
    }

    public boolean modificarCarrera(Carrera carrera) throws BDException {
        String query = "UPDATE carrera SET codigo = ?, nombre = ?, duracion_anios = ? WHERE id_carrera = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, carrera.getCodigo());
            ps.setString(2, carrera.getNombre());
            ps.setInt(3, carrera.getDuracionAnios());
            ps.setInt(4, carrera.getIdCarrera());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la carrera: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idCarrera, boolean estado) throws BDException {
        String query = "UPDATE carrera SET estado = ? WHERE id_carrera = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idCarrera);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado de la carrera: " + e.getMessage(), e);
        }
    }

    public List<Carrera> listar(int pagina, int tamanoPagina) throws BDException {
        List<Carrera> listaCarreras = new ArrayList<>();
        String query = "SELECT * FROM carrera LIMIT ? OFFSET ?";

        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tamanoPagina);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Carrera carrera = new Carrera(
                    rs.getInt("id_carrera"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getInt("duracion_anios"),
                    rs.getBoolean("estado"));

                    listaCarreras.add(carrera);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar las carreras paginadas: " + e.getMessage(), e);
        }

        return listaCarreras;
    }
}
