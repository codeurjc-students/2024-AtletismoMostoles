package com.example.service3.dto;

import java.util.Set;

public class EventNotification {

    private Long eventoId;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String ubicacion;
    private Set<Long> disciplineIds;

    public EventNotification() {}

    public EventNotification(Long eventoId, String titulo, String descripcion, String fecha, String ubicacion, Set<Long> disciplineIds) {
        this.eventoId = eventoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.disciplineIds = disciplineIds;
    }

    // Getters and setters
    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Set<Long> getDisciplineIds() {
        return disciplineIds;
    }

    public void setDisciplineIds(Set<Long> disciplineIds) {
        this.disciplineIds = disciplineIds;
    }
}
