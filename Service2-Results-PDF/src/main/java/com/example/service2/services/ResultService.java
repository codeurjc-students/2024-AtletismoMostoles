package com.example.resultados.service;

import com.example.service2.entities.Result;

import java.util.List;

public interface ResultService {

    Result saveResult(Result result);

    void saveAll(List<Result> results);

    List<Result> getResultsByAthleteId(Long athleteId);

    List<Result> getResultsByEventId(Long eventId);

    List<Result> getAllResults();
}
