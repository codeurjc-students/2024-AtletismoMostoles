package com.example.service1.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Coach {

    @Id
    @NotBlank
    private String licenseNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    @ManyToMany
    @JoinTable(
            name = "coach_disciplines",
            joinColumns = @JoinColumn(name = "coach_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    @JsonIgnoreProperties({"coaches", "events", "athletes", "equipment"} )
    private Set<Discipline> disciplines = new HashSet<>();

    @OneToMany(mappedBy = "coach")
    @JsonIgnoreProperties({"coach", "results", "disciplines"})
    private Set<Athlete> athletes = new HashSet<>();

    public Coach() {}

    public Coach(String licenseNumber, String firstName, String lastName, List<Discipline> disciplines) {
        this.licenseNumber = licenseNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.disciplines = new HashSet<>(disciplines);
    }

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

    public Set<Discipline> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(Set<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    public Set<Athlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(Set<Athlete> athletes) {
        this.athletes = athletes;
    }
}