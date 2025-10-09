package com.vocatio.dto.response;

import java.math.BigDecimal;

public class UniversidadesItemDTO {
    public Long id;
    public String name;
    public String city;
    public String programDuration;   // ej: "5 años"
    public Tuition tuition;          // currency + approxMonthly
    public String type;              // "public" | "private" (según tabla)
    public String moreInfoUrl;

    public static class Tuition {
        public String currency;        // "PEN"
        public BigDecimal approxMonthly;
    }
}
