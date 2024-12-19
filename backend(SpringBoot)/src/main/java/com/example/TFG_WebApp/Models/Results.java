package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;


@Entity
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties("results")
    private Athlete athlete;

    @ManyToOne
    @JsonIgnoreProperties("results")
    private Discipline discipline;

    @ManyToOne
    @JsonIgnoreProperties("results")
    private Event event;

    public Results() {}
    public Results(Athlete athlete, Discipline discipline, Event event) {
        this.athlete = athlete;
        this.discipline = discipline;
        this.event = event;
    }
    // Getters and setters
}
