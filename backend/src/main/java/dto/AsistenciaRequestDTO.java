package dto;

import java.time.LocalDate;

public class AsistenciaRequestDTO {
    private int idEstudiante;
    private int idAsignacion;
    private LocalDate fecha;
    private String estado; 

    public AsistenciaRequestDTO() {}

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
}