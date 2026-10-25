package dto;

import enums.ConceptoPago;

public class PagoRequestDTO {

    private String numeroFactura;
    private int idEstudiante;
    private int idCiclo;
    private ConceptoPago concepto;
    private Double monto;
    private String descripcion;
    private Integer mesColegiatura;
    
    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(int idCiclo) {
        this.idCiclo = idCiclo;
    }

    public ConceptoPago getConcepto() {
        return concepto;
    }

    public void setConcepto(ConceptoPago concepto) {
        this.concepto = concepto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getMesColegiatura() {
        return mesColegiatura;
    }

    public void setMesColegiatura(Integer mesColegiatura) {
        this.mesColegiatura = mesColegiatura;
    }
}
