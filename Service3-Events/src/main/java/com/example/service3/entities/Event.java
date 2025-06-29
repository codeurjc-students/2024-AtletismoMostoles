package com.example.service3.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private LocalDate date;

    private String mapLink;

    private String imageLink;

    @NotNull
    private boolean isOrganizedByClub;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_discipline", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "discipline_id")
    private Set<Long> disciplineIds = new HashSet<>();


    public Event() {}

    public Event(String name, LocalDate date, String mapLink, String imageLink, boolean isOrganizedByClub, Set<Long> disciplineIds) {
        this.name = name;
        this.date = date;
        this.mapLink = mapLink;
        this.imageLink = imageLink;
        this.isOrganizedByClub = isOrganizedByClub;
        this.disciplineIds = disciplineIds;
    }

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
        return isOrganizedByClub;
    }

    public void setOrganizedByClub(boolean organizedByClub) {
        isOrganizedByClub = organizedByClub;
    }

    public Set<Long> getDisciplineIds() {
        return disciplineIds;
    }

    public void setDisciplineIds(Set<Long> disciplineIds) {
        this.disciplineIds = disciplineIds;
    }
}
