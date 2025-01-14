package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Services.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

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
            @RequestParam(defaultValue = "date") String sortBy,
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
    public ResponseEntity<Results> createResult(@RequestBody Results result) {
        Results createdResult = resultService.createResult(result);
        return new ResponseEntity<>(createdResult, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Results> updateResult(@PathVariable Long id, @RequestBody Results result) {
        Results updatedResult = resultService.updateResult(id, result);
        return ResponseEntity.ok(updatedResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }
}