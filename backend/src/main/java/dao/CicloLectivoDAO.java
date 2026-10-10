package dao;

import dbconection.DBConnectionSingleton;
import excepciones.BDException;
import modelos.CicloLectivo;
import enums.EstadoCiclo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CicloLectivoDAO {

    public boolean crearCiclo(CicloLectivo ciclo) throws BDException {
        String query = "INSERT INTO ciclo_lectivo (anio, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, ciclo.getAnio());
            ps.setObject(2, ciclo.getFechaInicio());
            ps.setObject(3, ciclo.getFechaFin());
            ps.setString(4, ciclo.getEstado().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al crear el ciclo lectivo: " + e.getMessage(), e);
        }
    }

    public boolean abrirCiclo(int idCiclo) throws BDException {
        String queryVerificar = "SELECT COUNT(*) FROM ciclo_lectivo WHERE estado = 'ABIERTO'";
        String queryAbrir = "UPDATE ciclo_lectivo SET estado = 'ABIERTO' WHERE id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection()) {
            try (PreparedStatement psVerificar = connection.prepareStatement(queryVerificar); ResultSet rs = psVerificar.executeQuery()) {

                if (rs.next() && rs.getInt(1) > 0) {
                    throw new BDException("No se puede abrir el ciclo. Ya existe un ciclo lectivo activo actualmente. Debe cerrarlo primero.");
                }
            }

            try (PreparedStatement psAbrir = connection.prepareStatement(queryAbrir)) {
                psAbrir.setInt(1, idCiclo);
                return psAbrir.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error en la base de datos al intentar abrir el ciclo: " + e.getMessage(), e);
        }
    }

    public boolean cerrarCiclo(int idCiclo) throws BDException {
        String query = "UPDATE ciclo_lectivo SET estado = 'CERRADO' WHERE id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idCiclo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cerrar el ciclo lectivo: " + e.getMessage(), e);
        }
    }

    public Optional<CicloLectivo> obtenerCicloActivo() throws BDException {
        String query = "SELECT * FROM ciclo_lectivo WHERE estado = 'ABIERTO' LIMIT 1";
        CicloLectivo ciclo = null;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                ciclo = new CicloLectivo();
                ciclo.setIdCiclo(rs.getInt("id_ciclo"));
                ciclo.setAnio(rs.getInt("anio"));
                ciclo.setFechaInicio(rs.getObject("fecha_inicio", LocalDate.class));
                ciclo.setFechaFin(rs.getObject("fecha_fin", LocalDate.class));
                ciclo.setEstado(EstadoCiclo.valueOf(rs.getString("estado")));
            }

        } catch (SQLException e) {
            throw new BDException("Error al obtener el ciclo lectivo activo: " + e.getMessage(), e);
        }

        return Optional.ofNullable(ciclo);
    }

    public List<CicloLectivo> listarPaginado(int pagina, int tamanoPagina) throws BDException {
        List<CicloLectivo> listaCiclos = new ArrayList<>();
        String query = "SELECT * FROM ciclo_lectivo ORDER BY anio DESC LIMIT ? OFFSET ?";

        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tamanoPagina);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CicloLectivo ciclo = new CicloLectivo(
                            (rs.getInt("id_ciclo")),
                            (rs.getInt("anio")),
                            (rs.getObject("fecha_inicio", LocalDate.class)),
                            (rs.getObject("fecha_fin", LocalDate.class)),
                            (EstadoCiclo.valueOf(rs.getString("estado"))));

                    listaCiclos.add(ciclo);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los ciclos lectivos: " + e.getMessage(), e);
        }

        return listaCiclos;
    }

    public boolean modificarCiclo(CicloLectivo ciclo) throws BDException {
        String query = "UPDATE ciclo_lectivo SET anio = ?, fecha_inicio = ?, fecha_fin = ? WHERE id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, ciclo.getAnio());
            ps.setObject(2, ciclo.getFechaInicio());
            ps.setObject(3, ciclo.getFechaFin());
            ps.setInt(4, ciclo.getIdCiclo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el ciclo lectivo: " + e.getMessage(), e);
        }
    }
}
