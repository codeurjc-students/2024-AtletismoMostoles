package com.example.service3.dto;

import java.util.Set;

public class EventNotification {

    private Long eventoId;
    private String name;
    private String date;
    private String mapLink;
    private String imageLink;
    private boolean organizedByClub;
    private Set<Long> disciplineIds;

    public EventNotification() {}

    public EventNotification(Long eventoId, String name, String date, String mapLink,
                             String imageLink, boolean organizedByClub, Set<Long> disciplineIds) {
        this.eventoId = eventoId;
        this.name = name;
        this.date = date;
        this.mapLink = mapLink;
        this.imageLink = imageLink;
        this.organizedByClub = organizedByClub;
        this.disciplineIds = disciplineIds;
    }

    // Getters and setters
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMapLink() { return mapLink; }
    public void setMapLink(String mapLink) { this.mapLink = mapLink; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public boolean isOrganizedByClub() { return organizedByClub; }
    public void setOrganizedByClub(boolean organizedByClub) { this.organizedByClub = organizedByClub; }

    public Set<Long> getDisciplineIds() { return disciplineIds; }
    public void setDisciplineIds(Set<Long> disciplineIds) { this.disciplineIds = disciplineIds; }
}
