package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Seccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeccionDAO {

    private DBConection conexionDB = new DBConection();
    
    public Optional<Seccion> buscarPorGradoEIdentificador(int idGrado, String identificador) throws BDException {
        String query = "SELECT * FROM seccion WHERE id_grado = ? AND identificador = ?";
        Seccion seccion = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idGrado);
            ps.setString(2, identificador);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seccion = new Seccion(
                    rs.getInt("id_seccion"),
                    rs.getString("identificador"),
                    rs.getInt("cupo"),
                    rs.getInt("id_grado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al validar duplicidad de la sección: " + e.getMessage(), e);
        }

        return Optional.ofNullable(seccion);
    }

    public boolean registrarSeccion(Seccion seccion) throws BDException {
        String query = "INSERT INTO seccion (identificador, cupo, id_grado) VALUES (?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, seccion.getIdentificador());
            ps.setInt(2, seccion.getCupo());
            ps.setInt(3, seccion.getIdGrado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la sección: " + e.getMessage(), e);
        }
    }

    public boolean modificarSeccion(Seccion seccion) throws BDException {
        String query = "UPDATE seccion SET identificador = ?, cupo = ? WHERE id_seccion = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, seccion.getIdentificador());
            ps.setInt(2, seccion.getCupo());
            ps.setInt(3, seccion.getIdSeccion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la sección: " + e.getMessage(), e);
        }
    }

    public Optional<Seccion> buscarPorId(int idSeccion) throws BDException {
        String query = "SELECT * FROM seccion WHERE id_seccion = ?";
        Seccion seccion = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seccion = new Seccion(
                    rs.getInt("id_seccion"),
                    rs.getString("identificador"),
                    rs.getInt("cupo"),
                    rs.getInt("id_grado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la sección por ID: " + e.getMessage(), e);
        }

        return Optional.ofNullable(seccion);
    }

    public List<Seccion> listarPorGrado(int idGrado) throws BDException {
        List<Seccion> listaSecciones = new ArrayList<>();
        String query = "SELECT * FROM seccion WHERE id_grado = ? ORDER BY identificador ASC"; 

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idGrado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Seccion seccion = new Seccion(
                    rs.getInt("id_seccion"),
                    rs.getString("identificador"),
                    rs.getInt("cupo"),
                    rs.getInt("id_grado"));

                    listaSecciones.add(seccion);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar las secciones por grado: " + e.getMessage(), e);
        }

        return listaSecciones;
    }
}
