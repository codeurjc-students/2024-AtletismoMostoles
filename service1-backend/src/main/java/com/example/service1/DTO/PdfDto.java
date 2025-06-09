package com.example.service1.DTO;

import java.time.LocalDateTime;

public class PdfDto {
    private String requestId;
    private LocalDateTime timestampGenerado;
    private String urlBlob;
    private String estado;

    public PdfDto() { }

    public PdfDto(String requestId, LocalDateTime timestampGenerado, String urlBlob, String estado) {
        this.requestId = requestId;
        this.timestampGenerado = timestampGenerado;
        this.urlBlob = urlBlob;
        this.estado = estado;
    }

    // getters y setters...
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public LocalDateTime getTimestampGenerado() { return timestampGenerado; }
    public void setTimestampGenerado(LocalDateTime timestampGenerado) { this.timestampGenerado = timestampGenerado; }
    public String getUrlBlob() { return urlBlob; }
    public void setUrlBlob(String urlBlob) { this.urlBlob = urlBlob; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
