package dto;

public class ActividadRequestDTO {
    private int idAsignacion;
    private String nombre;
    private String descripcion;
    private double ponderacion;

    public ActividadRequestDTO() {}

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