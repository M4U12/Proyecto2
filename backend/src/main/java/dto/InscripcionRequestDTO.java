package dto;

public class InscripcionRequestDTO {

    private int idEstudiante;
    private int idGrado;
    private int idSeccion;
    private int idCiclo;

    public InscripcionRequestDTO() {
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    
}
