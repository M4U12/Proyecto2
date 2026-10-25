package dto;

import java.math.BigDecimal;

public class DetalleMesDTO {

    private int mesId;
    private String nombreMes;
    private String estado; // "PAGADO", "PENDIENTE", "ATRASADO"
    private double monto;
    private String fechaPago;
    private String numeroFactura;

    public int getMesId() {
        return mesId;
    }

    public void setMesId(int mesId) {
        this.mesId = mesId;
    }

    public String getNombreMes() {
        return nombreMes;
    }

    public void setNombreMes(String nombreMes) {
        this.nombreMes = nombreMes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }
}
