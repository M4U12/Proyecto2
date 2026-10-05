package modelos;

public class Carrera {

    private int idCarrera;
    private String codigo;
    private String nombre;
    private int duracionAnios;
    private boolean estado;

    public Carrera() {
    }

    public Carrera(int idCarrera, String codigo, String nombre, int duracionAnios, boolean estado) {
        this.idCarrera = idCarrera;
        this.codigo = codigo;
        this.nombre = nombre;
        this.duracionAnios = duracionAnios;
        this.estado = estado;
    }

    public int getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(int idCarrera) {
        this.idCarrera = idCarrera;
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

    public int getDuracionAnios() {
        return duracionAnios;
    }

    public void setDuracionAnios(int duracionAnios) {
        this.duracionAnios = duracionAnios;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
