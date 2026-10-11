package dao;

import dbconection.DBConnectionSingleton;
import dto.MallaCurricularDTO;
import excepciones.BDException;
import modelos.Curso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurriculoDAO {

    public boolean definirMallaCurricular(int idGrado, int idCiclo, List<Integer> idsCursos) throws BDException {
        // limpiamos la malla anterior para ese grado y ciclo específico
        String queryDelete = "DELETE FROM curriculo WHERE id_grado = ? AND id_ciclo = ?";
        // la nueva lista
        String queryInsert = "INSERT INTO curriculo (id_grado, id_ciclo, id_curso) VALUES (?, ?, ?)";

        Connection connection = null;

        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false); 

            // eliminar asignaciones previas
            try (PreparedStatement psDelete = connection.prepareStatement(queryDelete)) {
                psDelete.setInt(1, idGrado);
                psDelete.setInt(2, idCiclo);
                psDelete.executeUpdate();
            }

            // insertar la nueva lista de cursos si la lista no está vacía
            if (idsCursos != null && !idsCursos.isEmpty()) {
                try (PreparedStatement psInsert = connection.prepareStatement(queryInsert)) {
                    for (Integer idCurso : idsCursos) {
                        psInsert.setInt(1, idGrado);
                        psInsert.setInt(2, idCiclo);
                        psInsert.setInt(3, idCurso);
                        psInsert.addBatch(); 
                    }
                    psInsert.executeBatch(); 
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
            throw new BDException("Error al definir la malla curricular: " + e.getMessage(), e);

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

    public List<Curso> obtenerCursosPorGradoYCiclo(int idGrado, int idCiclo) throws BDException {
        List<Curso> listaCursos = new ArrayList<>();

        String query = "SELECT c.* FROM curso c "
                + "INNER JOIN curriculo cur ON c.id_curso = cur.id_curso "
                + "WHERE cur.id_grado = ? AND cur.id_ciclo = ?";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idCiclo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Curso curso = new Curso(
                    rs.getInt("id_curso"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getBoolean("estado"));

                    listaCursos.add(curso);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al obtener los cursos del currículo: " + e.getMessage(), e);
        }

        return listaCursos;
    }
    
    public List<MallaCurricularDTO> obtenerMallaCompletaPorCiclo(int idCiclo) throws BDException {
        List<MallaCurricularDTO> mallaCompleta = new ArrayList<>();

        String query = "SELECT g.id_grado, g.nombre AS grado_nombre, " +
                       "c.id_curso, c.codigo, c.nombre AS curso_nombre, c.estado " +
                       "FROM curriculo cur " +
                       "INNER JOIN grado g ON cur.id_grado = g.id_grado " +
                       "INNER JOIN curso c ON cur.id_curso = c.id_curso " +
                       "WHERE cur.id_ciclo = ? " +
                       "ORDER BY g.id_nivel, g.id_grado, c.codigo";

        try (Connection connection = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idCiclo);
            try (ResultSet rs = ps.executeQuery()) {
                
                MallaCurricularDTO gradoActual = null;

                while (rs.next()) {
                    int idGradoFila = rs.getInt("id_grado");

                    if (gradoActual == null || gradoActual.getIdGrado() != idGradoFila) {
                        
                        gradoActual = new MallaCurricularDTO();
                        gradoActual.setIdGrado(idGradoFila);
                        gradoActual.setGrado(rs.getString("grado_nombre"));
                        gradoActual.setCursos(new ArrayList<>());
                        
                        mallaCompleta.add(gradoActual);
                    }

                    Curso curso = new Curso(
                        rs.getInt("id_curso"),
                        rs.getString("codigo"),
                        rs.getString("curso_nombre"),
                        rs.getBoolean("estado")
                    );
                    
                    gradoActual.getCursos().add(curso);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al obtener la malla completa: " + e.getMessage(), e);
        }

        return mallaCompleta;
    }
}
