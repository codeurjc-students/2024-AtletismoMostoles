package com.example.TFG_WebApp.Models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private Date date;
    @ManyToMany(mappedBy = "events")
    private List<Discipline> disciplines;
    @OneToMany(mappedBy = "event")
    private List<Results> results;

}
