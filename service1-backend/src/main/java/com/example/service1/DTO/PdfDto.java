package com.example.service1.DTO;

import java.time.Instant;

public class PdfDto {
    private String requestId;
    private String athleteId;
    private Long eventId;
    private Instant timestampGenerated;
    private String urlBlob;
    private String state;

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Instant getTimestampGenerated() {
        return timestampGenerated;
    }
    public void setTimestampGenerated(Instant timestampGenerated) {
        this.timestampGenerated = timestampGenerated;
    }

    public String getUrlBlob() {
        return urlBlob;
    }
    public void setUrlBlob(String urlBlob) {
        this.urlBlob = urlBlob;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}
