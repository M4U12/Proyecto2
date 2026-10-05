package modelos;

import enums.EstadoCiclo;
import java.time.LocalDate;

public class CicloLectivo {
    private int idCiclo;
    private int anio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoCiclo estado;

    public CicloLectivo() {
    }

    public CicloLectivo(int idCiclo, int anio, LocalDate fechaInicio, LocalDate fechaFin, EstadoCiclo estado) {
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoCiclo getEstado() {
        return estado;
    }

    public void setEstado(EstadoCiclo estado) {
        this.estado = estado;
    }
}
