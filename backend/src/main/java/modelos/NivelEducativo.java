package modelos;

import enums.Nivel;

public class NivelEducativo {
    private int idNivel;
    private Nivel nivel;

    public NivelEducativo() {
    }

    public NivelEducativo(int idNivel, Nivel nivel) {
        this.idNivel = idNivel;
        this.nivel = nivel;
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
}
