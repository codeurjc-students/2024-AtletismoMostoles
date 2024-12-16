package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Athlete {

    @Id
    private String license;
    private String name;
    private String lastname;
    @ManyToOne
    private Coach coach;
    @OneToMany(mappedBy = "athlete")
    private List<Results> results;
    @ManyToMany(mappedBy = "athletes")
    private List<Discipline> disciplines;
    private String category;
    private String link_image;

    public Athlete() {}

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public List<Discipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLink_image() {
        return link_image;
    }

    public void setLink_image(String link_image) {
        this.link_image = link_image;
    }
}
