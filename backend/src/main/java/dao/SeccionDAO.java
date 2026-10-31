package dao;

import dbconection.DBConnectionSingleton;
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
    
    public Optional<Seccion> buscarPorGradoCicloEIdentificador(int idGrado, int idCiclo, String identificador) throws BDException {
        String query = "SELECT * FROM seccion WHERE id_grado = ? AND id_ciclo = ? AND identificador = ?";
        Seccion seccion = null;
        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);
            ps.setString(3, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seccion = new Seccion();
                    seccion.setIdSeccion(rs.getInt("id_seccion"));
                    seccion.setIdentificador(rs.getString("identificador"));
                    seccion.setCupo(rs.getInt("cupo"));
                    seccion.setIdGrado(rs.getInt("id_grado"));
                    seccion.setIdCiclo(rs.getInt("id_ciclo"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al validar duplicidad de la sección: " + e.getMessage(), e);
        }
        return Optional.ofNullable(seccion);
    }

    public boolean registrarSeccion(Seccion seccion) throws BDException {
        String query = "INSERT INTO seccion (identificador, cupo, id_grado, id_ciclo) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, seccion.getIdentificador());
            ps.setInt(2, seccion.getCupo());
            ps.setInt(3, seccion.getIdGrado());
            ps.setInt(4, seccion.getIdCiclo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al registrar la sección: " + e.getMessage(), e);
        }
    }

    public boolean modificarSeccion(Seccion seccion) throws BDException {
        String query = "UPDATE seccion SET identificador = ?, cupo = ? WHERE id_seccion = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

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

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seccion = new Seccion();
                    seccion.setIdSeccion(rs.getInt("id_seccion"));
                    seccion.setIdentificador(rs.getString("identificador"));
                    seccion.setCupo(rs.getInt("cupo"));
                    seccion.setIdGrado(rs.getInt("id_grado"));
                    seccion.setIdCiclo(rs.getInt("id_ciclo")); 
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la sección por ID: " + e.getMessage(), e);
        }

        return Optional.ofNullable(seccion);
    }

    public List<Seccion> listarPorGradoYCiclo(int idGrado, int idCiclo) throws BDException {
        List<Seccion> listaSecciones = new ArrayList<>();
        String query = "SELECT * FROM seccion WHERE id_grado = ? AND id_ciclo = ? ORDER BY identificador ASC"; 
        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Seccion seccion = new Seccion();
                    seccion.setIdSeccion(rs.getInt("id_seccion"));
                    seccion.setIdentificador(rs.getString("identificador"));
                    seccion.setCupo(rs.getInt("cupo"));
                    seccion.setIdGrado(rs.getInt("id_grado"));
                    seccion.setIdCiclo(rs.getInt("id_ciclo"));
                    listaSecciones.add(seccion);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar las secciones: " + e.getMessage(), e);
        }
        return listaSecciones;
    }
    
    public boolean eliminarSeccion(int idSeccion) throws BDException {
        String query = "DELETE FROM seccion WHERE id_seccion = ?";
        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, idSeccion);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            // aquí es se detendrá el borrado si hay alumnos inscritos 
            throw new BDException("No se puede eliminar la sección. Es posible que ya existan alumnos inscritos en ella.", e);
        }
    }
}
