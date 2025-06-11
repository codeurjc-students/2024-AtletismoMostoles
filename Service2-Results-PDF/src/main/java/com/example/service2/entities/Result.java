package com.example.service2.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long atletaId;
    private Long eventoId;
    private double marca;
    private LocalDate fecha;

    // Getters y setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAtletaId() {
        return atletaId;
    }
    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public double getMarca() {
        return marca;
    }
    public void setMarca(double marca) {
        this.marca = marca;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
