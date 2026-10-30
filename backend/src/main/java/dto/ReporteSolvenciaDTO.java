package dto;

public class ReporteSolvenciaDTO {

    private String codigoPersonal;
    private String nombreCompleto;
    private String estadoFinanciero; // "SOLVENTE" o "MOROSO"
    private String detalleDeuda;     // "Debe colegiatura de Mayo" o "Al día"

    public String getCodigoPersonal() {
        return codigoPersonal;
    }

    public void setCodigoPersonal(String codigoPersonal) {
        this.codigoPersonal = codigoPersonal;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEstadoFinanciero() {
        return estadoFinanciero;
    }

    public void setEstadoFinanciero(String estadoFinanciero) {
        this.estadoFinanciero = estadoFinanciero;
    }

    public String getDetalleDeuda() {
        return detalleDeuda;
    }

    public void setDetalleDeuda(String detalleDeuda) {
        this.detalleDeuda = detalleDeuda;
    }
}
