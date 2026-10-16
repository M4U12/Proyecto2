package dto;

import enums.EstadoEstudiante;
import java.time.LocalDate;

public class ExpedienteCompletoDTO {

    private int idEstudiante;
    private int idGradoActual;
    private int idCicloActivo;
    private String codigoPersonal;
    private String nombres;
    private String apellidos;
    private int edad; 
    private String informacionMedica;
    private String contactoEncargado;
    private String nombreGradoActual;
    private String identificadorSeccion;
    private int anioCicloActivo;
    private LocalDate fechaNacimiento;
    private String nombreEncargado;
    private EstadoEstudiante estadoAlumno; 

    public ExpedienteCompletoDTO() {
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

    public int getEdad() {
        return edad;
    }

    public String getInformacionMedica() {
        return informacionMedica;
    }

    public String getContactoEncargado() {
        return contactoEncargado;
    }

    public String getNombreGradoActual() {
        return nombreGradoActual;
    }

    public String getIdentificadorSeccion() {
        return identificadorSeccion;
    }

    public int getAnioCicloActivo() {
        return anioCicloActivo;
    }

    public EstadoEstudiante getEstadoAlumno() {
        return estadoAlumno;
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

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setInformacionMedica(String informacionMedica) {
        this.informacionMedica = informacionMedica;
    }

    public void setContactoEncargado(String contactoEncargado) {
        this.contactoEncargado = contactoEncargado;
    }

    public void setNombreGradoActual(String nombreGradoActual) {
        this.nombreGradoActual = nombreGradoActual;
    }

    public void setIdentificadorSeccion(String identificadorSeccion) {
        this.identificadorSeccion = identificadorSeccion;
    }

    public void setAnioCicloActivo(int anioCicloActivo) {
        this.anioCicloActivo = anioCicloActivo;
    }

    public void setEstadoAlumno(EstadoEstudiante estadoAlumno) {
        this.estadoAlumno = estadoAlumno;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getNombreEncargado() {
        return nombreEncargado;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setNombreEncargado(String nombreEncargado) {
        this.nombreEncargado = nombreEncargado;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }
    
    public int getIdGradoActual() {
        return idGradoActual;
    }

    public void setIdGradoActual(int idGradoActual) {
        this.idGradoActual = idGradoActual;
    }
    
    public int getIdCicloActivo() {
        return idCicloActivo;
    }

    public void setIdCicloActivo(int idCicloActivo) {
        this.idCicloActivo = idCicloActivo;
    }
}
