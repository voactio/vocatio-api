package com.vocatio.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class GraficoInteresDTO {
    private Map<String, Integer> puntajes;
}