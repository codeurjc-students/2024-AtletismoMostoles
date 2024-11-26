package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Discipline {

    @Id
    private String name;
    @ManyToMany
    @JoinTable(
            name = "discipline_athlete",
            joinColumns = @JoinColumn(name = "discipline"),
            inverseJoinColumns = @JoinColumn(name = "athletes_id"))
    private List<Athlete> athletes;
    @OneToMany(mappedBy = "discipline")
    private List<Coach> coaches;
    @OneToMany(mappedBy = "discipline")
    private List<Equipment> equipment;
    @ManyToMany
    @JoinTable(
            name = "discipline_event",
            joinColumns = @JoinColumn(name = "discipline"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
    private String link_img;
    private String class_schedule;

}
