package dto;

public class ReporteRendimientoDTO {

    private String curso;
    private Double promedioGeneral;
    private Double porcentajeAprobados;
    private Double porcentajeReprobados;

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public Double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(Double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Double getPorcentajeAprobados() {
        return porcentajeAprobados;
    }

    public void setPorcentajeAprobados(Double porcentajeAprobados) {
        this.porcentajeAprobados = porcentajeAprobados;
    }

    public Double getPorcentajeReprobados() {
        return porcentajeReprobados;
    }

    public void setPorcentajeReprobados(Double porcentajeReprobados) {
        this.porcentajeReprobados = porcentajeReprobados;
    }
}
