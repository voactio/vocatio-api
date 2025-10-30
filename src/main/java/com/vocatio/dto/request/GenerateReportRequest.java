package com.vocatio.dto.request;

import java.util.UUID;

public class GenerateReportRequest {
    private UUID testId;

    public UUID getTestId() { return testId; }
    public void setTestId(UUID testId) { this.testId = testId; }
}