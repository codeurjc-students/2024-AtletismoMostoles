package com.example.service1.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private String imageLink;

    @ManyToMany
    @JoinTable(
            name = "discipline_equipment",
            joinColumns = @JoinColumn(name = "discipline_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    @JsonIgnoreProperties("disciplines")
    private Set<Equipment> equipment = new HashSet<>();

    @ManyToMany(mappedBy = "disciplines")
    @JsonIgnoreProperties({"disciplines", "coach", "results"})
    private Set<Athlete> athletes = new HashSet<>();

    @ManyToMany(mappedBy = "disciplines")
    @JsonIgnoreProperties({"disciplines", "athletes"})
    private Set<Coach> coaches = new HashSet<>();

    public Discipline() {}

    public Discipline(String name, String description, List<Equipment> equipment, String imageLink) {
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
        this.equipment = new HashSet<>(equipment);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Set<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }

    public Set<Athlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(Set<Athlete> athletes) {
        this.athletes = athletes;
    }

    public Set<Coach> getCoaches() {
        return coaches;
    }

    public void setCoaches(Set<Coach> coaches) {
        this.coaches = coaches;
    }
}