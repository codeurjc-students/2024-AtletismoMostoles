package com.example.service2.dto;

public class PdfConfirmationMessage {
    private String athleteId;
    private String url;

    public PdfConfirmationMessage() {}

    public PdfConfirmationMessage(String athleteId, String url) {
        this.athleteId = athleteId;
        this.url = url;
    }

    public String getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

