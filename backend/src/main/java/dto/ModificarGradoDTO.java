package dto;

public class ModificarGradoDTO {

    private String nombre;
    private int idNivel;
    private Integer idCarrera;
    private Double cuotaInscripcion;
    private Double cuotaMensual;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Integer idCarrera) {
        this.idCarrera = idCarrera;
    }

    public Double getCuotaInscripcion() {
        return cuotaInscripcion;
    }

    public Double getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaInscripcion(Double cuotaInscripcion) {
        this.cuotaInscripcion = cuotaInscripcion;
    }

    public void setCuotaMensual(Double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }
    
    
}
