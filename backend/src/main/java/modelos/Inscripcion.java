package modelos;

import java.time.LocalDate;

public class Inscripcion {

    private int idInscripcion;
    private int idEstudiante;
    private int idGrado;
    private int idSeccion;
    private int idCiclo;
    private LocalDate fechaInscripcion;

    public Inscripcion() {
    }
    
    public Inscripcion(int idInscripcion, int idEstudiante, int idGrado, int idSeccion, int idCiclo, LocalDate fechaInscripcion) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrado = idGrado;
        this.idSeccion = idSeccion;
        this.idCiclo = idCiclo;
        this.fechaInscripcion = fechaInscripcion;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    
}
