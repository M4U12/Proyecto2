package modelos;

public class AsignacionMaestro {

    private int idAsignacion;
    private int idEmpleado;
    private int idCurso;
    private int idSeccion;
    private int idCiclo;

    public AsignacionMaestro() {
    }
    
    public AsignacionMaestro(int idAsignacion, int idEmpleado, int idCurso, int idSeccion, int idCiclo) {
        this.idAsignacion = idAsignacion;
        this.idEmpleado = idEmpleado;
        this.idCurso = idCurso;
        this.idSeccion = idSeccion;
        this.idCiclo = idCiclo;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

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

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
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
