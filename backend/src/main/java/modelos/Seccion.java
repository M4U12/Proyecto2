package modelos;

public class Seccion {
    private int idSeccion;
    private String identificador;
    private int cupo;
    private int idGrado;
    private int idCiclo;

    public Seccion() {
    }

    public Seccion(int idSeccion, String identificador, int cupo, int idGrado) {
        this.idSeccion = idSeccion;
        this.identificador = identificador;
        this.cupo = cupo;
        this.idGrado = idGrado;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getCupo() {
        return cupo;
    }

    public void setCupo(int cupo) {
        this.cupo = cupo;
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
    
}
