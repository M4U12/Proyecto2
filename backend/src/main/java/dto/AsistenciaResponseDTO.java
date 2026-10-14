package dto;

import java.time.LocalDate;

public class AsistenciaResponseDTO {
    private int idAsistencia;
    private int idEstudiante;
    private String nombres;
    private String apellidos;
    private LocalDate fecha;
    private String estado;

    public AsistenciaResponseDTO() {}

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
}