package dto;

public class AsignacionRequestDTO {
    private int idEmpleado;
    private int idCurso;
    private int idSeccion;
    private int idCiclo;

    public AsignacionRequestDTO() {}

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    
}