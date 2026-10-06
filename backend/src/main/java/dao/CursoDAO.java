package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursoDAO {

    private DBConection conexionDB = new DBConection();
    
    public Optional<Curso> buscarPorCodigo(String codigo) throws BDException {
        String query = "SELECT * FROM curso WHERE codigo = ?";
        Curso curso = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setCodigo(rs.getString("codigo"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setEstado(rs.getBoolean("estado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el curso por código: " + e.getMessage(), e);
        }

        return Optional.ofNullable(curso);
    }

    public boolean crearCurso(Curso curso) throws BDException {
        String query = "INSERT INTO curso (codigo, nombre, estado) VALUES (?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, curso.getCodigo());
            ps.setString(2, curso.getNombre());
            ps.setBoolean(3, curso.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el curso: " + e.getMessage(), e);
        }
    }

    public boolean modificarCurso(Curso curso) throws BDException {
        String query = "UPDATE curso SET codigo = ?, nombre = ? WHERE id_curso = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, curso.getCodigo());
            ps.setString(2, curso.getNombre());
            ps.setInt(3, curso.getIdCurso());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al modificar el curso: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstado(int idCurso, boolean estado) throws BDException {
        String query = "UPDATE curso SET estado = ? WHERE id_curso = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idCurso);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del curso: " + e.getMessage(), e);
        }
    }


    public List<Curso> listar(int pagina, int tamanoPagina) throws BDException {
        List<Curso> listaCursos = new ArrayList<>();
        String query = "SELECT * FROM curso LIMIT ? OFFSET ?";

        int offset = (pagina - 1) * tamanoPagina;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tamanoPagina);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setCodigo(rs.getString("codigo"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setEstado(rs.getBoolean("estado"));

                    listaCursos.add(curso);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los cursos paginados: " + e.getMessage(), e);
        }

        return listaCursos;
    }


    public Optional<Curso> buscarPorId(int idCurso) throws BDException {
        String query = "SELECT * FROM curso WHERE id_curso = ?";
        Curso curso = null;

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idCurso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setCodigo(rs.getString("codigo"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setEstado(rs.getBoolean("estado"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el curso por ID: " + e.getMessage(), e);
        }

        return Optional.ofNullable(curso);
    }
}
