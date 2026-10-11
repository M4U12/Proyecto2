package dto;

import java.util.List;
import modelos.Curso;

public class MallaCurricularDTO {

    private int idGrado;
    private String grado;
    private List<Curso> cursos;

    public MallaCurricularDTO() {
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public List<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
    }
}
