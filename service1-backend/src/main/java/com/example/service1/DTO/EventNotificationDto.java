package com.example.service1.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventNotificationDto {
    private Long eventoId;
    private String name;
    private LocalDate date;
    private String mapLink;
    private String imageLink;
    private boolean organizedByClub;
    private Set<Long> disciplineIds = new HashSet<>();
    private String timestampNotificacion;

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long id) {
        this.eventoId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(String mapLink) {
        this.mapLink = mapLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public boolean isOrganizedByClub() {
        return organizedByClub;
    }

    public void setOrganizedByClub(boolean organizedByClub) {
        this.organizedByClub = organizedByClub;
    }

    public String getTimestampNotificacion() {
        return timestampNotificacion;
    }

    public void setTimestampNotificacion(String timestampNotificacion) {
        this.timestampNotificacion = timestampNotificacion;
    }

    public Set<Long> getDisciplineIds() {
        return disciplineIds;
    }

    public void setDisciplineIds(Set<Long> disciplineIds) {
        this.disciplineIds = disciplineIds;
    }
}
