package com.vocatio.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class ResultadoTestDTO {
    private GraficoInteresDTO graficoIntereses;
    private List<CarreraRankingDTO> rankingCarreras;
}