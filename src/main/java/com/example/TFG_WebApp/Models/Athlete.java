package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Athlete {

    @Id
    private String license;
    private String name;
    private String surname;
    @ManyToOne
    private Coach coach;
    @OneToMany(mappedBy = "athlete")
    private List<Results> results;
    @ManyToMany(mappedBy = "athletes")
    private List<Discipline> disciplines;
    private String category;

    public Athlete() {}


}
