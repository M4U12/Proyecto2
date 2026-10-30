package dto;

public class ReporteLibrosMoraDTO {

    private String tituloLibro;
    private String codigoLibro;
    private String deudor;
    private String tipoDeudor;
    private String fechaLimite;
    private Integer diasRetraso;
    private Double recargoGenerado;

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getDeudor() {
        return deudor;
    }

    public void setDeudor(String deudor) {
        this.deudor = deudor;
    }

    public String getTipoDeudor() {
        return tipoDeudor;
    }

    public void setTipoDeudor(String tipoDeudor) {
        this.tipoDeudor = tipoDeudor;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Integer getDiasRetraso() {
        return diasRetraso;
    }

    public void setDiasRetraso(Integer diasRetraso) {
        this.diasRetraso = diasRetraso;
    }

    public Double getRecargoGenerado() {
        return recargoGenerado;
    }

    public void setRecargoGenerado(Double recargoGenerado) {
        this.recargoGenerado = recargoGenerado;
    }
}
