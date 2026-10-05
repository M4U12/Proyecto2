package modelos;

import enums.Rol;

public class Usuario {
    private int idUsuario;
    private String correo;
    private String password;
    private Rol rol;
    private boolean estado;
    private Integer idEmpleado; 
    private Integer idEstudiante; 

    public Usuario() {
    }

    public Usuario(int idUsuario, String correo, String password, Rol rol, boolean estado, Integer idEmpleado, Integer idEstudiante) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.idEmpleado = idEmpleado;
        this.idEstudiante = idEstudiante;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }
}
