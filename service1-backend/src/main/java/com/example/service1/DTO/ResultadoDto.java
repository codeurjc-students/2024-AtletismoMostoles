package com.example.service1.DTO;


public class ResultadoDto {
    private Long id;
    private Long atletaId;
    private Long eventoId;
    private Long disciplinaId;
    private String valor;

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

    public Long getDisciplinaId() {
        return disciplinaId;
    }
    public void setDisciplinaId(Long disciplinaId) {
        this.disciplinaId = disciplinaId;
    }
    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }

}
