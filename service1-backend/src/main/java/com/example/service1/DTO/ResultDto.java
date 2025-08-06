package com.example.service1.DTO;


public class ResultDto {
    private Long id;
    private String athleteId;
    private Long eventId;
    private Long disciplineId;
    private String value;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getAthleteId() {
        return athleteId;
    }
    public void setAthleteId(String athleteId) {
        this.athleteId = athleteId;
    }

    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getDisciplineId() {
        return disciplineId;
    }
    public void setDisciplineId(Long disciplineId) {
        this.disciplineId = disciplineId;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
