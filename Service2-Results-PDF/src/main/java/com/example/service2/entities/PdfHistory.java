package com.example.service2.entities;

import jakarta.persistence.*;

@Entity
public class PdfHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long athleteId;

    private String url;

    // Constructors
    public PdfHistory() {}

    public PdfHistory(Long athleteId, String url) {
        this.athleteId = athleteId;
        this.url = url;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
