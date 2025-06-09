package com.example.service1.DTO;

import java.time.LocalDate;

public class ResultadoDto {
    private Long id;
    private Long atletaId;
    private Long eventoId;
    private double marca;
    private LocalDate fecha;

    public ResultadoDto() { }

    public ResultadoDto(Long id, Long atletaId, Long eventoId, double marca, LocalDate fecha) {
        this.id = id;
        this.atletaId = atletaId;
        this.eventoId = eventoId;
        this.marca = marca;
        this.fecha = fecha;
    }

    // getters y setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAtletaId() { return atletaId; }
    public void setAtletaId(Long atletaId) { this.atletaId = atletaId; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public double getMarca() { return marca; }
    public void setMarca(double marca) { this.marca = marca; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
