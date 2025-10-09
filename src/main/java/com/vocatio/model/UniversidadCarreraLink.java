package com.vocatio.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "universidad_carreras")
public class UniversidadCarreraLink {

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "id_universidad_bigint")
        private Long universidadId;

        @Column(name = "id_carrera_bigint")
        private Long carreraId;

        public Id() {}
        public Id(Long universidadId, Long carreraId) {
            this.universidadId = universidadId;
            this.carreraId = carreraId;
        }
        // getters/setters/equals/hashCode
        // ...
        @Override public boolean equals(Object o){ if(this==o)return true; if(!(o instanceof Id i))return false; return Objects.equals(universidadId,i.universidadId)&&Objects.equals(carreraId,i.carreraId);}
        @Override public int hashCode(){ return Objects.hash(universidadId, carreraId); }
        public Long getUniversidadId(){return universidadId;}
        public void setUniversidadId(Long v){this.universidadId=v;}
        public Long getCarreraId(){return carreraId;}
        public void setCarreraId(Long v){this.carreraId=v;}
    }

    @EmbeddedId
    private Id id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("universidadId")
    @JoinColumn(name = "id_universidad_bigint")
    private Universidad universidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carreraId")
    @JoinColumn(name = "id_carrera_bigint")
    private Carrera carrera;

    @Column(name = "costo_por_ano")
    private BigDecimal costoPorAno;

    @Column(name = "url_plan_especifico")
    private String urlPlanEspecifico;

    // getters/setters
    public Id getId(){return id;}
    public void setId(Id id){this.id=id;}
    public Universidad getUniversidad(){return universidad;}
    public void setUniversidad(Universidad u){this.universidad=u;}
    public Carrera getCarrera(){return carrera;}
    public void setCarrera(Carrera c){this.carrera=c;}
    public BigDecimal getCostoPorAno(){return costoPorAno;}
    public void setCostoPorAno(BigDecimal v){this.costoPorAno=v;}
    public String getUrlPlanEspecifico(){return urlPlanEspecifico;}
    public void setUrlPlanEspecifico(String v){this.urlPlanEspecifico=v;}
}
