package com.example.service2.dto;

public class PdfGenerationRequest {
    private String athleteId;

    public PdfGenerationRequest() {}

    public PdfGenerationRequest(String athleteId) {
        this.athleteId = athleteId;
    }

    public String getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }
}

