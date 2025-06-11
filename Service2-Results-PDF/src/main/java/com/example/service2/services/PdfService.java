package com.example.service2.services;

import com.example.service2.entities.PdfRequest;

import java.util.List;

public interface PdfService {

    /**
     * Generate a PDF containing the results of a specific athlete.
     *
     * @param atletaId the ID of the athlete
     */
    void generarPdfParaAtleta(Long atletaId);

    /**
     * Generate a PDF containing the results of a specific event.
     *
     * @param eventoId the ID of the event
     */
    void generarPdfParaEvento(Long eventoId);

    List<PdfRequest> findHistorialPorAtletaId(Long atletaId);
}
