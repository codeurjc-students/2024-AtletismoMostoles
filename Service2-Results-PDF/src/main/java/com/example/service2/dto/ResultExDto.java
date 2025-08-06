package com.example.service2.dto;

public class ResultExDto {
    private final String athleteFullName;
    private final String eventName;
    private final String disciplineName;
    private final String value;

    public ResultExDto(String athleteFullName, String eventName, String disciplineName, String value) {
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

}
