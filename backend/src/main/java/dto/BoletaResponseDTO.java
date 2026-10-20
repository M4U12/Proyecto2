package dto;

public class BoletaResponseDTO {
    private String nombreActividad;
    private double ponderacion;
    private Double punteoObtenido; 

    public String getNombreActividad() {
        return nombreActividad;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public Double getPunteoObtenido() {
        return punteoObtenido;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public void setPunteoObtenido(Double punteoObtenido) {
        this.punteoObtenido = punteoObtenido;
    }

    
}