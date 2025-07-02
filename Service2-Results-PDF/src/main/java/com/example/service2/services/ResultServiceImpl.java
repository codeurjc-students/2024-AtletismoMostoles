package com.example.service2.services;

import com.example.service2.entities.Result;
import com.example.service2.repositories.ResultRepository;
import com.example.resultados.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultsRepository;

    @Override
    public Result saveResult(Result result) {
        return null;
    }

    @Override
    public void saveAll(List<Result> results) {

    }

    @Override
    public List<Result> getResultsByAthleteId(Long athleteId) {
        return List.of();
    }

    @Override
    public List<Result> getResultsByEventId(Long eventId) {
        return resultsRepository.findByEventId(eventId);
    }

    @Override
    public List<Result> getAllResults() {
        return resultsRepository.findAll();
    }
}
