package dto;

import java.util.List;

public class CurriculoRequestDTO {

    private int idGrado;
    private int idCiclo;
    private List<Integer> idsCursos;

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    public List<Integer> getIdsCursos() {
        return idsCursos;
    }

    public void setIdsCursos(List<Integer> idsCursos) {
        this.idsCursos = idsCursos;
    }
}
