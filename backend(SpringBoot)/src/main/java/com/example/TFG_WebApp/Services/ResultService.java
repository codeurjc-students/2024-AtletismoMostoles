package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Repositories.ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.transform.Result;


@Service
public class ResultService {

    @Autowired
    private ResultsRepository resultsRepository;

    public Results createResult(Results result) {
        return resultsRepository.save(result);
    }

    public Results updateResult(Long id, Results updatedResult) {
        return resultsRepository.findById(id).map(existingResult -> {
            existingResult.setAthlete(updatedResult.getAthlete());
            existingResult.setDiscipline(updatedResult.getDiscipline());
            existingResult.setEvent(updatedResult.getEvent());
            return resultsRepository.save(existingResult);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
    }

    public void deleteResult(Long id) {
        try {
            resultsRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found");
        }
    }

    public Results getResultById(Long id) {
        return resultsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Result not found"));
    }

    public Page<Results> getPaginatedResults(Pageable pageable) {
        return resultsRepository.findAll(pageable);
    }

    public Page<Results> getFilteredResults(Long athleteId, Long eventId, Long disciplineId, Pageable pageable) {
        return resultsRepository.findAll((Specification<Results>) (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (athleteId != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.join("athlete").get("id"), athleteId));
            }
            if (eventId != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.join("event").get("id"), eventId));
            }
            if (disciplineId != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.join("discipline").get("id"), disciplineId));
            }

            return predicates;
        }, pageable);
    }
}
