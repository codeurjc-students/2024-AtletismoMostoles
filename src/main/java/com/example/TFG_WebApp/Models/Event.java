package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private LocalDate date;
    @ManyToMany(mappedBy = "events")
    private List<Discipline> disciplines;
    @OneToMany(mappedBy = "event")
    private List<Results> results;
    private Boolean organizers;
    private String link_image = ".../static/images/FAM.jpg";
    private String link_map;


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Boolean getOrganizers() {
        return organizers;
    }

    public void setOrganizers(Boolean organizers) {
        this.organizers = organizers;
    }

    public String getLink_image() {
        return link_image;
    }

    public void setLink_image(String link_image) {
        this.link_image = link_image;
    }
}
