package com.example.service1.DTO;

import java.io.Serial;
import java.io.Serializable;

public class PdfGenerationRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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
