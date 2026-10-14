package dto;

public class NotaResponseDTO {
    private int idNota;
    private int idEstudiante;
    private String nombres;
    private String apellidos;
    private String nombreActividad;
    private double punteoObtenido;

    public NotaResponseDTO() {}

    public int getIdNota() {
        return idNota;
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

    public String getNombreActividad() {
        return nombreActividad;
    }

    public double getPunteoObtenido() {
        return punteoObtenido;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
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

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public void setPunteoObtenido(double punteoObtenido) {
        this.punteoObtenido = punteoObtenido;
    }

    
}