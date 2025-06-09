package com.example.service1.DTO;

import java.time.LocalDateTime;

public class NotificacionDto {
    private Long eventoId;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String ubicacion;
    private LocalDateTime timestampNotificacion;

    public NotificacionDto() { }

    public NotificacionDto(Long eventoId, String titulo, String descripcion,
                           LocalDateTime fechaInicio, LocalDateTime fechaFin,
                           String ubicacion, LocalDateTime timestampNotificacion) {
        this.eventoId = eventoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ubicacion = ubicacion;
        this.timestampNotificacion = timestampNotificacion;
    }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
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
    public LocalDateTime getTimestampNotificacion() { return timestampNotificacion; }
    public void setTimestampNotificacion(LocalDateTime timestampNotificacion) { this.timestampNotificacion = timestampNotificacion; }
}
