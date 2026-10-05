package modelos;

public class Curriculo {
    private int idCurriculo;
    private int idGrado;
    private int idCiclo;
    private int idCurso;

    public Curriculo() {
    }

    public Curriculo(int idCurriculo, int idGrado, int idCiclo, int idCurso) {
        this.idCurriculo = idCurriculo;
        this.idGrado = idGrado;
        this.idCiclo = idCiclo;
        this.idCurso = idCurso;
    }

    public int getIdCurriculo() {
        return idCurriculo;
    }

    public void setIdCurriculo(int idCurriculo) {
        this.idCurriculo = idCurriculo;
    }

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

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }
}
