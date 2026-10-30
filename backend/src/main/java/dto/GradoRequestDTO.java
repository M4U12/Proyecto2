package dto;

import java.util.List;

public class GradoRequestDTO {

    private String nombre;
    private int idNivel;
    private Integer idCarrera;
    private List<SeccionDTO> secciones;
    private int idCiclo;
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

    public List<SeccionDTO> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<SeccionDTO> secciones) {
        this.secciones = secciones;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    public double getCuotaMensual() {
        return cuotaMensual;
    }

    public double getCuotaInscripcion() {
        return cuotaInscripcion;
    }

    public void setCuotaMensual(double cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public void setCuotaInscripcion(double cuotaInscripcion) {
        this.cuotaInscripcion = cuotaInscripcion;
    }
    
    
}
