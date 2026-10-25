package dto;

import java.util.List;

public class EstadoCuentaDTO {

    private boolean inscripcionPagada;
    private List<DetalleMesDTO> colegiaturas;
    private List<OtroPagoDTO> otrosPagos;

    public boolean isInscripcionPagada() {
        return inscripcionPagada;
    }

    public void setInscripcionPagada(boolean inscripcionPagada) {
        this.inscripcionPagada = inscripcionPagada;
    }

    public List<DetalleMesDTO> getColegiaturas() {
        return colegiaturas;
    }

    public void setColegiaturas(List<DetalleMesDTO> colegiaturas) {
        this.colegiaturas = colegiaturas;
    }

    public List<OtroPagoDTO> getOtrosPagos() {
        return otrosPagos;
    }

    public void setOtrosPagos(List<OtroPagoDTO> otrosPagos) {
        this.otrosPagos = otrosPagos;
    }
}
