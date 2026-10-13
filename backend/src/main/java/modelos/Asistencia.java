package modelos;

import enums.EstadoAsistencia;
import java.time.LocalDate;

public class Asistencia {

    private int idAsistencia;
    private int idEstudiante;
    private int idAsignacion;
    private LocalDate fecha;
    private EstadoAsistencia estado; 

    public Asistencia() {
    }

    public Asistencia(int idAsistencia, int idEstudiante, int idAsignacion, LocalDate fecha, EstadoAsistencia estado) {
        this.idAsistencia = idAsistencia;
        this.idEstudiante = idEstudiante;
        this.idAsignacion = idAsignacion;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
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

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    
}
