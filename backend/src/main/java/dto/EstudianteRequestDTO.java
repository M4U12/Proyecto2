package dto;

import java.time.LocalDate;

public class EstudianteRequestDTO {

    private String codigoPersonal;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String informacionMedica;
    private String nombreEncargado;
    private String contactoEncargado;

    public EstudianteRequestDTO() {
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

    
}
