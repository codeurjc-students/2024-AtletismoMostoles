package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDate date;
    private String locationLink;
    private boolean organizedByClub;
    private String imageLink;

    @ManyToMany(mappedBy = "events")
    @JsonIgnoreProperties("events")
    private List<Discipline> disciplines;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Results> results;


    public Event(String name, LocalDate date, boolean organizedByClub, Optional<String> imageLink, String locationLink) {
        this.name = name;
        this.date = date;
        this.organizedByClub = organizedByClub;
        imageLink.ifPresent(s -> this.imageLink = s);
        this.locationLink = locationLink;
    }

    public Event() {

    }

    // Getters and setters

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

    public String getLocationLink() {
        return locationLink;
    }

    public void setLocationLink(String locationLink) {
        this.locationLink = locationLink;
    }

    public boolean isOrganizedByClub() {
        return organizedByClub;
    }

    public void setOrganizedByClub(boolean organizedByClub) {
        this.organizedByClub = organizedByClub;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public List<Discipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }
}