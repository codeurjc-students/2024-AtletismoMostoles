package com.example.service1.DTO;

public class PdfGenerationRequest {
    private Long athleteId;

    public PdfGenerationRequest() {}

    public PdfGenerationRequest(Long athleteId) {
        this.athleteId = athleteId;
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(Long athleteId) {
        this.athleteId = athleteId;
    }
}
