package modelos;

import enums.EstadoEstudiante;
import java.time.LocalDate;

public class Estudiante {

    private int idEstudiante;
    private String codigoPersonal;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String informacionMedica;
    private String nombreEncargado;
    private String contactoEncargado;
    private EstadoEstudiante estado;

    public Estudiante() {
    }

    public Estudiante(int idEstudiante, String codigoPersonal, String nombres, String apellidos, LocalDate fechaNacimiento, String informacionMedica, String nombreEncargado, String contactoEncargado, EstadoEstudiante estado) {
        this.idEstudiante = idEstudiante;
        this.codigoPersonal = codigoPersonal;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.informacionMedica = informacionMedica;
        this.nombreEncargado = nombreEncargado;
        this.contactoEncargado = contactoEncargado;
        this.estado = estado;
    }

    public Estudiante(String codigoPersonal, String nombres, String apellidos, LocalDate fechaNacimiento, String informacionMedica, String nombreEncargado, String contactoEncargado, EstadoEstudiante estado) {
        this.codigoPersonal = codigoPersonal;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.informacionMedica = informacionMedica;
        this.nombreEncargado = nombreEncargado;
        this.contactoEncargado = contactoEncargado;
        this.estado = estado;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public String getCodigoPersonal() {
        return codigoPersonal;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getInformacionMedica() {
        return informacionMedica;
    }

    public String getNombreEncargado() {
        return nombreEncargado;
    }

    public String getContactoEncargado() {
        return contactoEncargado;
    }

    public EstadoEstudiante getEstado() {
        return estado;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void setCodigoPersonal(String codigoPersonal) {
        this.codigoPersonal = codigoPersonal;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setInformacionMedica(String informacionMedica) {
        this.informacionMedica = informacionMedica;
    }

    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }

    public void setContactoEncargado(String contactoEncargado) {
        this.contactoEncargado = contactoEncargado;
    }

    public void setEstado(EstadoEstudiante estado) {
        this.estado = estado;
    }

    
}
