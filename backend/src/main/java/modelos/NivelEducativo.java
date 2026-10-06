package modelos;

import enums.Nivel;

public class NivelEducativo {
    private int idNivel;
    private Nivel nivel;
    private String descripcion;
    private boolean estado;

    public NivelEducativo() {
    }

    public NivelEducativo(int idNivel, Nivel nivel, String descripcion, boolean estado) {
        this.idNivel = idNivel;
        this.nivel = nivel;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public void setIdNivel(int idNivel) {
        this.idNivel = idNivel;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    
}
