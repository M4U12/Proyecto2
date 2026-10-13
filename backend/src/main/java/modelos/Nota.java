package modelos;

public class Nota {

    private int idNota;
    private int idActividad;
    private int idEstudiante;
    private double punteoObtenido;

    public Nota() {
    }
    
    public Nota(int idNota, int idActividad, int idEstudiante, double punteoObtenido) {
        this.idNota = idNota;
        this.idActividad = idActividad;
        this.idEstudiante = idEstudiante;
        this.punteoObtenido = punteoObtenido;
    }

    public int getIdNota() {
        return idNota;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public double getPunteoObtenido() {
        return punteoObtenido;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setPunteoObtenido(double punteoObtenido) {
        this.punteoObtenido = punteoObtenido;
    }

    
}
