package com.example.service2.entities;

import jakarta.persistence.*;

@Entity
public class PdfHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String athleteId;

    private String url;

    // Constructors
    public PdfHistory() {}

    public PdfHistory(String athleteId, String url) {
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
