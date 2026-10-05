package modelos;

public class Curso {
    private int idCurso;
    private String codigo;
    private String nombre;
    private boolean estado;

    public Curso() {
    }

    public Curso(int idCurso, String codigo, String nombre, boolean estado) {
        this.idCurso = idCurso;
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
