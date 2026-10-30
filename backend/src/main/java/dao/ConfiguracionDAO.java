package dao;

import dbconection.DBConnectionSingleton;
import dto.ConfiguracionDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionDAO {

    public List<ConfiguracionDTO> listarConfiguraciones() throws BDException {
        List<ConfiguracionDTO> lista = new ArrayList<>();
        String query = "SELECT clave, valor, descripcion FROM configuracion_sistema ORDER BY id_configuracion ASC";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ConfiguracionDTO dto = new ConfiguracionDTO();
                dto.setClave(rs.getString("clave"));
                dto.setValor(rs.getDouble("valor"));
                dto.setDescripcion(rs.getString("descripcion"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar configuraciones: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean actualizarValor(String clave, Double nuevoValor) throws BDException {
        String query = "UPDATE configuracion_sistema SET valor = ? WHERE clave = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDouble(1, nuevoValor);
            ps.setString(2, clave);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la configuración: " + e.getMessage(), e);
        }
    }
}
