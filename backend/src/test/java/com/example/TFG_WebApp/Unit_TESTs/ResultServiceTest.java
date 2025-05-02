package com.example.TFG_WebApp.Unit_TESTs;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Repositories.ResultsRepository;
import com.example.TFG_WebApp.Services.ResultService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResultServiceTest {

    @Mock
    private ResultsRepository resultRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    void testGetResults_All() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Results> resultsPage = new PageImpl<>(Collections.singletonList(new Results()));

        when(resultRepository.findAll(pageable)).thenReturn(resultsPage);

        Page<Results> result = resultService.getResults(pageable, null, null);

        assertEquals(1, result.getTotalElements());
        verify(resultRepository).findAll(pageable);
    }

    @Test
    void testGetResults_ByEventId() {
        Pageable pageable = PageRequest.of(0, 10);

        when(resultRepository.findByEventId(1L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Results())));

        Page<Results> result = resultService.getResults(pageable, 1L, null);

        assertEquals(1, result.getTotalElements());
        verify(resultRepository).findByEventId(1L, pageable);
    }

    @Test
    void testGetResults_ByDisciplineId() {
        Pageable pageable = PageRequest.of(0, 10);

        when(resultRepository.findByDisciplineId(1L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Results())));

        Page<Results> result = resultService.getResults(pageable, null, 1L);

        assertEquals(1, result.getTotalElements());
        verify(resultRepository).findByDisciplineId(1L, pageable);
    }

    @Test
    void testGetResults_ByEventAndDisciplineId() {
        Pageable pageable = PageRequest.of(0, 10);

        when(resultRepository.findByEventIdAndDisciplineId(1L, 2L, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new Results())));

        Page<Results> result = resultService.getResults(pageable, 1L, 2L);

        assertEquals(1, result.getTotalElements());
        verify(resultRepository).findByEventIdAndDisciplineId(1L, 2L, pageable);
    }

    @Test
    void testGetResultById_Success() {
        Results results = new Results();
        results.setId(1L);

        when(resultRepository.findById(1L)).thenReturn(Optional.of(results));

        Results result = resultService.getResultById(1L);

        assertEquals(1L, result.getId());
        verify(resultRepository).findById(1L);
    }

    @Test
    void testGetResultById_NotFound() {
        when(resultRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            resultService.getResultById(99L);
        });

        verify(resultRepository).findById(99L);
    }

    @Test
    void testCreateResult() {
        Results newResult = new Results();
        newResult.setValue(10.00);

        when(resultRepository.save(newResult)).thenReturn(newResult);

        Results result = resultService.createResult(newResult);

        assertEquals(10.0, result.getValue());
        verify(resultRepository).save(newResult);
    }

    @Test
    void testUpdateResult() {
        Results existing = new Results();
        existing.setId(1L);
        existing.setValue(8.00);

        Results updated = new Results();
        updated.setValue(9.50);

        when(resultRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(resultRepository.save(existing)).thenReturn(existing);

        Results result = resultService.updateResult(1L, updated);

        assertEquals(9.50, result.getValue());
        verify(resultRepository).save(existing);
    }

    @Test
    void testDeleteResult() {
        Results result = new Results();
        result.setId(1L);

        when(resultRepository.findById(1L)).thenReturn(Optional.of(result));
        doNothing().when(resultRepository).delete(result);

        resultService.deleteResult(1L);

        verify(resultRepository).delete(result);
    }

    @Test
    void testCreateMultipleResults() {
        List<Results> resultsList = Collections.singletonList(new Results());

        when(resultRepository.saveAll(resultsList)).thenReturn(resultsList);

        List<Results> result = resultService.createMultiple(resultsList);

        assertEquals(1, result.size());
        verify(resultRepository).saveAll(resultsList);
    }
}
