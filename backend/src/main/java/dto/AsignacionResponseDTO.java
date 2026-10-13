package dto;

public class AsignacionResponseDTO {
    private int idAsignacion;
    private String nombreMaestro;
    private String nombreCurso;
    private String grado;
    private String seccion;
    private int anioCiclo;

    public AsignacionResponseDTO() {}

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public String getNombreMaestro() {
        return nombreMaestro;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getAnioCiclo() {
        return anioCiclo;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public void setNombreMaestro(String nombreMaestro) {
        this.nombreMaestro = nombreMaestro;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public void setAnioCiclo(int anioCiclo) {
        this.anioCiclo = anioCiclo;
    }

    
}