package modelos;

import java.util.List;
import java.util.ArrayList;

public class Grado {
    private int idGrado;
    private String nombre;
    private int idNivel;
    private Integer idCarrera; 
    private List<Seccion> secciones;

    public Grado() {
        this.secciones = new ArrayList<>();
    }

    public Grado(int idGrado, String nombre, int idNivel, Integer idCarrera, List<Seccion> secciones) {
        this.idGrado = idGrado;
        this.nombre = nombre;
        this.idNivel = idNivel;
        this.idCarrera = idCarrera;
        this.secciones = secciones != null ? secciones : new ArrayList<>();
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Integer idCarrera) {
        this.idCarrera = idCarrera;
    }

    public List<Seccion> getSecciones() {
        return secciones;
    }

    public void setSecciones(List<Seccion> secciones) {
        this.secciones = secciones;
    }
}
