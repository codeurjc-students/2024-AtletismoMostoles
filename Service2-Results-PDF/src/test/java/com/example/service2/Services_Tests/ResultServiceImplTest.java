package com.example.service2.Services_Tests;

import com.example.service2.entities.Result;
import com.example.service2.repositories.ResultRepository;
import com.example.service2.services.ResultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ResultServiceImplTest {

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ResultServiceImpl resultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetResultsByAthleteId() {
        String athleteId = "123";
        List<Result> expected = List.of(new Result());
        when(resultRepository.findByAthleteId(athleteId)).thenReturn(expected);

        List<Result> results = resultService.getResultsByAthleteId(athleteId);

        assertEquals(expected, results);
        verify(resultRepository).findByAthleteId(athleteId);
    }

    @Test
    void testSaveResult() {
        Result result = new Result();
        when(resultRepository.save(result)).thenReturn(result);

        Result saved = resultService.saveResult(result);

        assertEquals(result, saved);
        verify(resultRepository).save(result);
    }

    @Test
    void testSaveAll() {
        List<Result> resultList = List.of(new Result());
        resultService.saveAll(resultList);

        verify(resultRepository).saveAll(resultList);
    }

    @Test
    void testGetResultsByEventId() {
        Long eventId = 10L;
        List<Result> expected = List.of(new Result());
        when(resultRepository.findByEventId(eventId)).thenReturn(expected);

        List<Result> results = resultService.getResultsByEventId(eventId);

        assertEquals(expected, results);
        verify(resultRepository).findByEventId(eventId);
    }

    @Test
    void testGetAllResults() {
        List<Result> expected = List.of(new Result(), new Result());
        when(resultRepository.findAll()).thenReturn(expected);

        List<Result> results = resultService.getAllResults();

        assertEquals(expected.size(), results.size());
        verify(resultRepository).findAll();
    }
}
