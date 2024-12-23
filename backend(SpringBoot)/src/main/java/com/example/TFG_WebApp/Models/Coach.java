package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;


@Entity
public class Coach {
    @Id
    private String licenseNumber;
    private String firstName;
    private String lastName;

    @ManyToOne
    @JsonIgnoreProperties("coaches")
    private Discipline discipline;

    @OneToMany(mappedBy = "coach", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Athlete> athletes;

    public Coach() {}
    public Coach(String firstName, String lastName, Discipline discipline) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.discipline = discipline;
    }

    // Getters and setters

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public List<Athlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<Athlete> athletes) {
        this.athletes = athletes;
    }
}
