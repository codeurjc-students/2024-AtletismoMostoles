package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Coach {

    @Id
    private String license;
    private String name;
    private String surname;
    @ManyToOne
    private Discipline discipline;
    @OneToMany(mappedBy = "coach")
    private List<Athlete> athletes;

}
