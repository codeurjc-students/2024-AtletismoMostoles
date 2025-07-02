package com.example.service2.dto;

public class PdfConfirmationMessage {
    private Long athleteId;
    private String url;

    public PdfConfirmationMessage() {}

    public PdfConfirmationMessage(Long athleteId, String url) {
        this.athleteId = athleteId;
        this.url = url;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

