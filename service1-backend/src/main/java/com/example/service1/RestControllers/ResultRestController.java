package com.example.service1.RestControllers;

import com.example.service1.DTO.PdfDto;
import com.example.service1.DTO.ResultDto;
import com.example.service1.Services.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultRestController {

    @Autowired
    private ResultService resultService;

    @GetMapping
    public ResponseEntity<Page<ResultDto>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultDto> allResults = resultService.getAllResults();
        return ResponseEntity.ok(paginate(allResults, page, size, sortBy));
    }

    @GetMapping("/athlete/{atletaId}")
    public ResponseEntity<Page<ResultDto>> getResultsByAthlete(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultDto> results = resultService.getResultsByAthlete(atletaId);
        return ResponseEntity.ok(paginate(results, page, size, sortBy));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Page<ResultDto>> getResultsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        List<ResultDto> results = resultService.getResultsByEvent(eventId);
        return ResponseEntity.ok(paginate(results, page, size, sortBy));
    }

    @GetMapping("/pdf/history/{atletaId}")
    public ResponseEntity<Page<PdfDto>> getHistorialPdf(
            @PathVariable String atletaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timestampGenerado") String sortBy
    ) {
        List<PdfDto> pdfs = resultService.getHistorialPdf(atletaId);
        return ResponseEntity.ok(paginate(pdfs, page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto> getResultById(@PathVariable Long id) {
        ResultDto result = resultService.getResultById(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ResultDto> createResult(@Valid @RequestBody ResultDto dto) {
        ResultDto saved = resultService.saveResult(
                dto.getAthleteId(), dto.getEventId(), dto.getDisciplineId(), dto.getValue());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ResultDto>> createMultipleResults(@RequestBody List<ResultDto> DTos) {
        List<ResultDto> saved = resultService.saveResultsBatchFromDto(DTos);
        return ResponseEntity.ok(saved);
    }


    @PostMapping("/pdf/{athleteId}")
    public ResponseEntity<Void> requestGenerationPdf(@PathVariable String athleteId) {
        resultService.requestGenerationPdf(athleteId);
        return ResponseEntity.accepted().build();
    }

    private <T> Page<T> paginate(List<T> items, int page, int size, String sortBy) {
        int start = Math.min(page * size, items.size());
        int end = Math.min(start + size, items.size());
        List<T> content = items.subList(start, end);
        return new PageImpl<>(content, PageRequest.of(page, size, Sort.by(sortBy)), items.size());
    }
}
