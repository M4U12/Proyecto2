package dao;

import dbconection.DBConnectionSingleton;
import excepciones.BDException;
import modelos.Grado;
import modelos.Seccion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradoDAO {
    public boolean registrarGradoConSecciones(Grado grado, List<Seccion> secciones) throws BDException {
        String queryGrado = "INSERT INTO grado (nombre, id_nivel, id_carrera) VALUES (?, ?, ?)";
        String querySeccion = "INSERT INTO seccion (identificador, cupo, id_grado) VALUES (?, ?, ?)";

        Connection connection = null;

        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psGrado = connection.prepareStatement(queryGrado, Statement.RETURN_GENERATED_KEYS)) {
                psGrado.setString(1, grado.getNombre());
                psGrado.setInt(2, grado.getIdNivel());

                if (grado.getIdCarrera() != null && grado.getIdCarrera() > 0) {
                    psGrado.setInt(3, grado.getIdCarrera());
                } else {
                    psGrado.setNull(3, Types.INTEGER);
                }

                psGrado.executeUpdate();

                ResultSet rs = psGrado.getGeneratedKeys();
                int idGradoGenerado = 0;
                if (rs.next()) {
                    idGradoGenerado = rs.getInt(1);
                }

                if (idGradoGenerado > 0 && secciones != null && !secciones.isEmpty()) {
                    try (PreparedStatement psSeccion = connection.prepareStatement(querySeccion)) {
                        for (Seccion seccion : secciones) {
                            psSeccion.setString(1, seccion.getIdentificador());
                            psSeccion.setInt(2, seccion.getCupo());
                            psSeccion.setInt(3, idGradoGenerado);
                            psSeccion.addBatch();
                        }
                        psSeccion.executeBatch();
                    }
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BDException("Error en la transacción al registrar grado y secciones: " + e.getMessage(), e);

        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean modificarGrado(Grado grado) throws BDException {
        String queryUpdateGrado = "UPDATE grado SET nombre = ?, id_nivel = ?, id_carrera = ? WHERE id_grado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement psGrado = connection.prepareStatement(queryUpdateGrado)) {

            psGrado.setString(1, grado.getNombre());
            psGrado.setInt(2, grado.getIdNivel());

            if (grado.getIdCarrera() != null && grado.getIdCarrera() > 0) {
                psGrado.setInt(3, grado.getIdCarrera());
            } else {
                psGrado.setNull(3, java.sql.Types.INTEGER);
            }
            psGrado.setInt(4, grado.getIdGrado());

            return psGrado.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el grado: " + e.getMessage(), e);
        }
    }

    public List<Grado> listarConSeccionesPorCiclo(int idCiclo) throws BDException {
        List<Grado> listaGrados = new ArrayList<>();
        String queryGrado = "SELECT DISTINCT g.* FROM grado g INNER JOIN curriculo c ON g.id_grado = c.id_grado WHERE c.id_ciclo = ?";
        String querySeccion = "SELECT * FROM seccion WHERE id_grado = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement psGrado = connection.prepareStatement(queryGrado)) {

            psGrado.setInt(1, idCiclo);

            try (ResultSet rsGrado = psGrado.executeQuery()) {
                while (rsGrado.next()) {
                    Grado grado = new Grado();
                    grado.setIdGrado(rsGrado.getInt("id_grado"));
                    grado.setNombre(rsGrado.getString("nombre"));
                    grado.setIdNivel(rsGrado.getInt("id_nivel"));

                    int idCarrera = rsGrado.getInt("id_carrera");
                    if (!rsGrado.wasNull()) {
                        grado.setIdCarrera(idCarrera);
                    }

                    // secciones especificas de este grado
                    List<Seccion> listaSecciones = new ArrayList<>();
                    try (PreparedStatement psSeccion = connection.prepareStatement(querySeccion)) {
                        psSeccion.setInt(1, grado.getIdGrado());
                        try (ResultSet rsSeccion = psSeccion.executeQuery()) {
                            while (rsSeccion.next()) {
                                Seccion seccion = new Seccion(
                                rsSeccion.getInt("id_seccion"),
                                rsSeccion.getString("identificador"),
                                rsSeccion.getInt("cupo"),
                                rsSeccion.getInt("id_grado"));

                                listaSecciones.add(seccion);
                            }
                        }
                    }

                    grado.setSecciones(listaSecciones);
                    listaGrados.add(grado);
                }
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar los grados y secciones por ciclo: " + e.getMessage(), e);
        }

        return listaGrados;
    }

    // 4. Buscar grado por ID
    public Optional<Grado> buscarPorId(int idGrado) throws BDException {
        String query = "SELECT * FROM grado WHERE id_grado = ?";
        Grado grado = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    grado = new Grado();
                    grado.setIdGrado(rs.getInt("id_grado"));
                    grado.setNombre(rs.getString("nombre"));
                    grado.setIdNivel(rs.getInt("id_nivel"));

                    int idCarrera = rs.getInt("id_carrera");
                    if (!rs.wasNull()) {
                        grado.setIdCarrera(idCarrera);
                    }
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el grado por ID: " + e.getMessage(), e);
        }
        return Optional.ofNullable(grado);
    }
}
