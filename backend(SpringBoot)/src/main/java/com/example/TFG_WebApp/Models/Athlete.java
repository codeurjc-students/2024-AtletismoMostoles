package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Athlete {
    @Id
    private String licenseNumber; // Unique identifier

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    @JsonIgnoreProperties({"athletes", "disciplines"})
    private Coach coach;

    @ManyToMany
    @JoinTable(
            name = "athlete_discipline",
            joinColumns = @JoinColumn(name = "athlete_id"),
            inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    @JsonIgnoreProperties({"athletes", "coaches", "equipment", "events"})
    private Set<Discipline> disciplines = new HashSet<>();

    @OneToMany(mappedBy = "athlete")
    @JsonIgnoreProperties({"athlete", "event"})
    private List<Results> results = new ArrayList<>();

    public Athlete(String licenseNumber, String firstName, String lastName, LocalDate birthDate, Coach coach, Set<Discipline> disciplines) {
        this.licenseNumber = licenseNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.coach = coach;
        this.disciplines = disciplines;
    }

    public Athlete() {

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
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