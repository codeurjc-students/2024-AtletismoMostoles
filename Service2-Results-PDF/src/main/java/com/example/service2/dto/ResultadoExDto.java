package com.example.service2.dto;

public class ResultadoExDto {
    private String athleteFullName;
    private String eventName;
    private String disciplineName;
    private String value;

    // constructor, getters y setters


    public ResultadoExDto(String athleteFullName, String eventName, String disciplineName, String value) {
        this.athleteFullName = athleteFullName;
        this.eventName = eventName;
        this.disciplineName = disciplineName;
        this.value = value;
    }

    public String getAthleteFullName() {
        return athleteFullName;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public String getValue() {
        return value;
    }

    public String setValue(String value) {
        this.value = value;
        return value;
    }

    public String setAthleteFullName(String athleteFullName) {
        this.athleteFullName = athleteFullName;
        return athleteFullName;
    }
    public String setEventName(String eventName) {
        this.eventName = eventName;
        return eventName;
    }
    public String setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
        return disciplineName;
    }
}
