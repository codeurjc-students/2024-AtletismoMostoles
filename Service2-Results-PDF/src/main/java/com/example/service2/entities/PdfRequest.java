package com.example.service2.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pdf_requests")
public class PdfRequest {

    @Id
    private String requestId;

    private Long atletaId;     // Nullable
    private Long eventoId;     // Nullable

    private Instant timestampGenerado;
    private String urlBlob;
    private String estado;

    // Getters and setters

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
