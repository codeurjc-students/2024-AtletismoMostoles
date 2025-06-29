package com.example.service1.DTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class EventDto {
    private Long id;
    private String name;
    private LocalDate date;
    private String mapLink;
    private String imageLink;
    private boolean organizedByClub;
    private Set<Long> disciplineIds = new HashSet<>();

    public EventDto() {}

    public EventDto(Long id, String name, LocalDate date, String mapLink, String imageLink, boolean organizedByClub, Set<Long> disciplineIds) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.mapLink = mapLink;
        this.imageLink = imageLink;
        this.organizedByClub = organizedByClub;
        this.disciplineIds = disciplineIds;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Long> getDisciplineIds() {
        return disciplineIds;
    }

    public void setDisciplineIds(Set<Long> disciplineIds) {
        this.disciplineIds = disciplineIds;
    }
}
