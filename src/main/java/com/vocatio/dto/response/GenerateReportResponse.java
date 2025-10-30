package com.vocatio.dto.response;

import java.time.Instant;

public class GenerateReportResponse {
    private String reportId;
    private String status;
    private String downloadUrl;
    private Instant createdAt;
    private Summary summary;

    public static class Summary {
        private String lastTestId;
        private int favoritesCount;
        private int materialsCount;

        public Summary() {}
        public Summary(String lastTestId, int favoritesCount, int materialsCount) {
            this.lastTestId = lastTestId;
            this.favoritesCount = favoritesCount;
            this.materialsCount = materialsCount;
        }
        public String getLastTestId() { return lastTestId; }
        public int getFavoritesCount() { return favoritesCount; }
        public int getMaterialsCount() { return materialsCount; }
        public void setLastTestId(String v) { this.lastTestId = v; }
        public void setFavoritesCount(int v) { this.favoritesCount = v; }
        public void setMaterialsCount(int v) { this.materialsCount = v; }
    }

    public String getReportId() { return reportId; }
    public String getStatus() { return status; }
    public String getDownloadUrl() { return downloadUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Summary getSummary() { return summary; }

    public void setReportId(String reportId) { this.reportId = reportId; }
    public void setStatus(String status) { this.status = status; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setSummary(Summary summary) { this.summary = summary; }
}