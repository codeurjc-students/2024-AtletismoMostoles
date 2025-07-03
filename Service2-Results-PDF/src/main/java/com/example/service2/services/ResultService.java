package com.example.service2.services;

import com.example.service2.entities.Result;

import java.util.List;

public interface ResultService {

    void saveResult(Result result);

    void saveAll(List<Result> results);

    List<Result> getResultsByAthleteId(String athleteId);

    List<Result> getResultsByEventId(Long eventId);

    List<Result> getAllResults();
}
