package com.example.service2.entities;

import jakarta.persistence.*;

@Entity
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;
    private Long disciplineId;
    private String athleteId;
    private String value;

    public Result() {}

    public Result(Long id, Long eventId, Long disciplineId, String athleteId, String value) {
        this.id = id;
        this.eventId = eventId;
        this.disciplineId = disciplineId;
        this.athleteId = athleteId;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getDisciplineId() {
        return disciplineId;
    }

    public String getAthleteId() {
        return athleteId;
    }

    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
