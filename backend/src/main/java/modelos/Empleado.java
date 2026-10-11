package modelos;

import java.time.LocalDate;

public class Empleado {
    private int idEmpleado;
    private String dpi;
    private String nombres;
    private String apellidos;
    private String puesto;
    private LocalDate fechaContratacion;
    private double salario;
    private boolean estado;
    private String rolSistema;

    public Empleado() {
    }

    public Empleado(int idEmpleado, String dpi, String nombres, String apellidos, String puesto, LocalDate fechaContratacion, double salario, boolean estado) {
        this.idEmpleado = idEmpleado;
        this.dpi = dpi;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.puesto = puesto;
        this.fechaContratacion = fechaContratacion;
        this.salario = salario;
        this.estado = estado;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    public String getRolSistema() {
        return rolSistema;
    }

    public void setRolSistema(String rolSistema) {
        this.rolSistema = rolSistema;
    }
}
