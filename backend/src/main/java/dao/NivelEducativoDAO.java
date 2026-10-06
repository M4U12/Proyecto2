package dao;

import dbconection.DBConection;
import excepciones.BDException;
import enums.Nivel;
import modelos.NivelEducativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NivelEducativoDAO {

    private DBConection conexionDB = new DBConection();

    public List<NivelEducativo> listarTodos() throws BDException {
        List<NivelEducativo> listaNiveles = new ArrayList<>();
        String query = "SELECT * FROM nivel_educativo";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NivelEducativo nivel = new NivelEducativo(
                rs.getInt("id_nivel"),
                Nivel.valueOf(rs.getString("nivel")),
                rs.getString("descripcion"),
                rs.getBoolean("estado"));

                listaNiveles.add(nivel);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar los niveles educativos: " + e.getMessage(), e);
        }

        return listaNiveles;
    }

    public Optional<NivelEducativo> buscarPorId(int idNivel) throws BDException {
        String query = "SELECT * FROM nivel_educativo WHERE id_nivel = ?";
        NivelEducativo nivel = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idNivel);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nivel = new NivelEducativo(
                    rs.getInt("id_nivel"),
                    Nivel.valueOf(rs.getString("nivel")),
                    rs.getString("descripcion"),
                    rs.getBoolean("estado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el nivel educativo: " + e.getMessage(), e);
        }

        return Optional.ofNullable(nivel);
    }

    public boolean modificarNivel(NivelEducativo nivel) throws BDException {
        String query = "UPDATE nivel_educativo SET descripcion = ? WHERE id_nivel = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, nivel.getDescripcion());
            ps.setInt(2, nivel.getIdNivel());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la descripción del nivel: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idNivel, boolean estado) throws BDException {
        String query = "UPDATE nivel_educativo SET estado = ? WHERE id_nivel = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idNivel);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del nivel educativo: " + e.getMessage(), e);
        }
    }
}
