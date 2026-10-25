package dto;

import enums.EstadoPrestamo;
import java.time.LocalDate;

public class PrestamoResponseDTO {

    private int idPrestamo;
    private int idLibro;
    private String codigoLibro;
    private String tituloLibro;
    private String nombreSolicitante;
    private String tipoSolicitante; // "ESTUDIANTE" o "MAESTRO"
    private LocalDate fechaSolicitud;
    private LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaDevolucion;
    private EstadoPrestamo estado;
    private int renovaciones;
    private double multaGenerada;
    private boolean multaPagada;
    private int idEstudiante;
    private int idEmpleado;

    public PrestamoResponseDTO() {
    }
    
    public PrestamoResponseDTO(int idPrestamo, int idLibro, String codigoLibro, String tituloLibro, String nombreSolicitante, String tipoSolicitante, LocalDate fechaSolicitud, LocalDate fechaPrestamo, LocalDate fechaLimite, LocalDate fechaDevolucion, EstadoPrestamo estado, int renovaciones, double multaGenerada, boolean multaPagada, int idEstudiante, int idEmpleado) {
        this.idPrestamo = idPrestamo;
        this.idLibro = idLibro;
        this.codigoLibro = codigoLibro;
        this.tituloLibro = tituloLibro;
        this.nombreSolicitante = nombreSolicitante;
        this.tipoSolicitante = tipoSolicitante;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.renovaciones = renovaciones;
        this.multaGenerada = multaGenerada;
        this.multaPagada = multaPagada;
        this.idEstudiante = idEstudiante;
        this.idEmpleado = idEmpleado;
    }

    
    
    public int getIdPrestamo() {
        return idPrestamo;
    }
    
    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public void setTipoSolicitante(String tipoSolicitante) {
        this.tipoSolicitante = tipoSolicitante;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPrestamo estado) {
        this.estado = estado;
    }

    public int getRenovaciones() {
        return renovaciones;
    }

    public void setRenovaciones(int renovaciones) {
        this.renovaciones = renovaciones;
    }

    public double getMultaGenerada() {
        return multaGenerada;
    }

    public void setMultaGenerada(double multaGenerada) {
        this.multaGenerada = multaGenerada;
    }

    public boolean isMultaPagada() {
        return multaPagada;
    }

    public void setMultaPagada(boolean multaPagada) {
        this.multaPagada = multaPagada;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
    
    
}
