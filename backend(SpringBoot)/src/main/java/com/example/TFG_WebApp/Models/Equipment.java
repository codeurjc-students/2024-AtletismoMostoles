package com.example.TFG_WebApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String imageLink;

    @ManyToMany(mappedBy = "equipment")
    @JsonIgnoreProperties("equipment")
    private List<Discipline> disciplines;

    public Equipment() {}
    public Equipment(String name, String description, String imageLink) {
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
    }
    // Getters and setters
}