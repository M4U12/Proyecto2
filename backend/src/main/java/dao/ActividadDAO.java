package dao;

import dbconection.DBConnectionSingleton;
import dto.ActividadRequestDTO;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            throw new BDException("Error al crear la actividad: " + e.getMessage(), e);
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
}