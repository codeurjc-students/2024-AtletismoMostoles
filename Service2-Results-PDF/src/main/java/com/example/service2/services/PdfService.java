package com.example.service2.services;

import java.util.List;

public interface PdfService {

    List<String> getUrlsByAthleteId(String athleteId);

    void generarPdfParaAtleta(String athleteId);

    void saveUrlForAthlete(String athleteId, String url);
}
