package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

@Entity
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String description;
    @ManyToOne
    private Discipline discipline;


}
