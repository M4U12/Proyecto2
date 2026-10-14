package dto;

public class ActividadResponseDTO {
    private int idActividad;
    private String nombre;
    private String descripcion;
    private double ponderacion;

    public ActividadResponseDTO() {}

    public int getIdActividad() {
        return idActividad;
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