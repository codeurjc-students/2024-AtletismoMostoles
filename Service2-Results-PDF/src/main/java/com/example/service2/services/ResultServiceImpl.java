package com.example.service2.services;

import com.example.service2.entities.Result;
import com.example.service2.repositories.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultRepository resultsRepository;

    @Override
    public void saveResult(Result result) {
        System.out.println("saveResult" + result);
        resultsRepository.save(result);
    }

    @Override
    public void saveAll(List<Result> results) {
        System.out.println("saveAll" + results);
        resultsRepository.saveAll(results);
    }

    @Override
    public List<Result> getResultsByAthleteId(String athleteId) {
        return resultsRepository.findByAthleteId(athleteId);
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
