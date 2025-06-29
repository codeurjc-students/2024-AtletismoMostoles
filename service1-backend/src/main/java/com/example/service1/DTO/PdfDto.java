package com.example.service1.DTO;

import java.time.Instant;

public class PdfDto {
    private String requestId;
    private Long atletaId;
    private Long eventoId;
    private Instant timestampGenerado;
    private String urlBlob;
    private String estado;

    // Getters y setters
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Instant getTimestampGenerado() {
        return timestampGenerado;
    }
    public void setTimestampGenerado(Instant timestampGenerado) {
        this.timestampGenerado = timestampGenerado;
    }

    public String getUrlBlob() {
        return urlBlob;
    }
    public void setUrlBlob(String urlBlob) {
        this.urlBlob = urlBlob;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
