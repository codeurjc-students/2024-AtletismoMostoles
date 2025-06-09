package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Repositories.ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import java.util.List;

@Service

public class ResultService {
    @Autowired
    private ResultsRepository resultRepository;

    public Page<Results> getResults(Pageable pageable, Long eventId, Long disciplineId) {
        if (eventId != null && disciplineId != null) {
            return resultRepository.findByEventIdAndDisciplineId(eventId, disciplineId, pageable);
        } else if (eventId != null) {
            return resultRepository.findByEventId(eventId, pageable);
        } else if (disciplineId != null) {
            return resultRepository.findByDisciplineId(disciplineId, pageable);
        } else {
            return resultRepository.findAll(pageable);
        }
    }

    public Results getResultById(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with ID: " + id));
    }

    public Results createResult(Results results) {
        return resultRepository.save(results);
    }

    public Results updateResult(Long id, Results updatedResult) {
        Results existingResult = getResultById(id);
        existingResult.setValue(updatedResult.getValue());
        existingResult.setEvent(updatedResult.getEvent());
        existingResult.setDiscipline(updatedResult.getDiscipline());
        existingResult.setAthlete(updatedResult.getAthlete());
        return resultRepository.save(existingResult);
    }

    public void deleteResult(Long id) {
        Results results = getResultById(id);
        resultRepository.delete(results);
    }

    public List<Results> createMultiple(List<Results> resultsList) {
        return resultRepository.saveAll(resultsList);
    }

}