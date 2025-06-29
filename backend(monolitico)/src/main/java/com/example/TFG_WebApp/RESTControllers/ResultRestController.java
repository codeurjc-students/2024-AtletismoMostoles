package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Services.ResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "https://localhost:4200")
public class ResultRestController {
    @Autowired
    private ResultService resultService;

    @GetMapping
    public ResponseEntity<Page<Results>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long disciplineId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Results> results = resultService.getResults(pageable, eventId, disciplineId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Results> getResult(@PathVariable Long id) {
        Results result = resultService.getResultById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Results> createResult(@Valid @RequestBody Results result) {
        Results createdResult = resultService.createResult(result);
        return new ResponseEntity<>(createdResult, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Results> updateResult(@PathVariable Long id,@Valid @RequestBody Results result) {
        Results updatedResult = resultService.updateResult(id, result);
        return ResponseEntity.ok(updatedResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createMultipleResults(@RequestBody List<Results> resultsList) {
        try {
            List<Results> saved = resultService.createMultiple(resultsList);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar los resultados: " + e.getMessage());
        }
    }
}