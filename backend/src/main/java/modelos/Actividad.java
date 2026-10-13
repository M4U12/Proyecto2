package modelos;

public class Actividad {

    private int idActividad;
    private int idAsignacion;
    private String nombre;
    private String descripcion;
    private double ponderacion;

    public Actividad() {
    }

    public Actividad(int idActividad, int idAsignacion, String nombre, String descripcion, double ponderacion) {
        this.idActividad = idActividad;
        this.idAsignacion = idAsignacion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ponderacion = ponderacion;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    
}
