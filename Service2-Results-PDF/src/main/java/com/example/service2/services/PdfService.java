package com.example.service2.services;

import java.util.List;

public interface PdfService {

    List<String> getUrlsByAthleteId(Long athleteId);

    void generarPdfParaAtleta(Long athleteId);

    void saveUrlForAthlete(Long athleteId, String url);
}
