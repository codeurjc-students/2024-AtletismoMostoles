package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.List;

@Entity
public class Athlete {
    @Id
    private String licenseNumber;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @ManyToOne
    @JsonIgnoreProperties("athletes")
    private Coach coach;

    @ManyToMany
    @JsonIgnoreProperties("athletes")
    private List<Discipline> disciplines;

    @OneToMany(mappedBy = "athlete", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Results> results;

    public Athlete() {}

    public Athlete(String licenseNumber, String firstName, String lastName, LocalDate birthDate, Coach coach){
        this.licenseNumber = licenseNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.coach = coach;
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