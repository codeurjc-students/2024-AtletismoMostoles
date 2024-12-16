package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class Results {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne
    private Athlete athlete;
    @ManyToOne
    private Event event;
    @OneToOne
    private Discipline discipline;
    private float marca;

}
