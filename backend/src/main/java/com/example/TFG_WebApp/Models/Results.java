package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "athlete_id")
    @JsonIgnoreProperties({"coach", "results", "disciplines"})
    private Athlete athlete;

    @ManyToOne
    @JoinColumn(name = "discipline_id")
    @JsonIgnoreProperties({"equipment", "coaches", "athletes", "events"})
    private Discipline discipline;

    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties({"results", "disciplines"})
    private Event event;

    private double value; // For storing the result value (e.g., time or distance)

    public Results(){}

    public Results(Athlete athlete, Discipline discipline, Event event, double value) {
        this.athlete = athlete;
        this.discipline = discipline;
        this.event = event;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}