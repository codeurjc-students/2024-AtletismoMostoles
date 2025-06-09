package com.example.service1.DTO;

import java.time.LocalDateTime;

public class EventDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String ubicacion;
    private Long creadoPor;
    private LocalDateTime timestampCreacion;

    public EventDto() { }

    public EventDto(Long id, String titulo, String descripcion,
                     LocalDateTime fechaInicio, LocalDateTime fechaFin,
                     String ubicacion, Long creadoPor, LocalDateTime timestampCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ubicacion = ubicacion;
        this.creadoPor = creadoPor;
        this.timestampCreacion = timestampCreacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public LocalDateTime getTimestampCreacion() { return timestampCreacion; }
    public void setTimestampCreacion(LocalDateTime timestampCreacion) { this.timestampCreacion = timestampCreacion; }
}
