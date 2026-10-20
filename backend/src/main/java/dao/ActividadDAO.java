package dao;

import dbconection.DBConnectionSingleton;
import dto.ActividadRequestDTO;
import dto.ActividadResponseDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    // Crear actividad (asignar zona)
    public boolean crearActividad(ActividadRequestDTO actividad) throws BDException {
        String query = "INSERT INTO actividad (id_asignacion, nombre, descripcion, ponderacion) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, actividad.getIdAsignacion());
            ps.setString(2, actividad.getNombre());
            ps.setString(3, actividad.getDescripcion());
            ps.setDouble(4, actividad.getPonderacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al crear la actividad: ", e);
        }
    }

    public boolean modificarActividad(int idActividad, ActividadRequestDTO actividad) throws BDException {
        String query = "UPDATE actividad SET nombre = ?, descripcion = ?, ponderacion = ? WHERE id_actividad = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, actividad.getNombre());
            ps.setString(2, actividad.getDescripcion());
            ps.setDouble(3, actividad.getPonderacion());
            ps.setInt(4, idActividad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar la actividad: " + e.getMessage(), e);
        }
    }
    
    public List<ActividadResponseDTO> listarActividadesPorAsignacion(int idAsignacion) throws BDException {
        List<ActividadResponseDTO> lista = new ArrayList<>();
        String query = "SELECT id_actividad, nombre, descripcion, ponderacion FROM actividad WHERE id_asignacion = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActividadResponseDTO act = new ActividadResponseDTO();
                    act.setIdActividad(rs.getInt("id_actividad"));
                    act.setNombre(rs.getString("nombre"));
                    act.setDescripcion(rs.getString("descripcion"));
                    act.setPonderacion(rs.getDouble("ponderacion"));
                    lista.add(act);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar actividades: " + e.getMessage(), e);
        }
        return lista;
    }
}