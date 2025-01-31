package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate date;
    private String mapLink;
    private boolean isOrganizedByClub;
    private String imageLink;

    @ManyToMany
    @JoinTable(
            name = "event_discipline",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    @JsonIgnoreProperties({"equipment", "coaches", "athletes", "events"})
    private Set<Discipline> disciplines = new HashSet<>();

    @OneToMany(mappedBy = "event")
    @JsonIgnoreProperties({"discipline", "athlete", "event"})
    private List<Results> results = new ArrayList<>();

    public Event() {}
    public Event(String name, LocalDate date, String mapLink, String imageLink, boolean isOrganizedByClub, List<Discipline> disciplines) {
        this.name = name;
        this.date = date;
        this.mapLink = mapLink;
        this.isOrganizedByClub = isOrganizedByClub;
        this.imageLink = imageLink;
        this.disciplines = new HashSet<>(disciplines);
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

    public boolean isOrganizedByClub() {
        return isOrganizedByClub;
    }

    public void setOrganizedByClub(boolean organizedByClub) {
        isOrganizedByClub = organizedByClub;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Set<Discipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(Set<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
}