package com.vocatio.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CompareCareersRequest {
    @NotNull
    private List<String> careerIds; // vendrán como strings; luego se parsean a Long

    public List<String> getCareerIds() { return careerIds; }
    public void setCareerIds(List<String> careerIds) { this.careerIds = careerIds; }
}
