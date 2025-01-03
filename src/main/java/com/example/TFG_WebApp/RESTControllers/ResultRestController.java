package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Results;
import com.example.TFG_WebApp.Services.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

@RestController
@RequestMapping("/api/results")
public class ResultRestController {

    @Autowired
    private ResultService resultService;

    @PostMapping
    public ResponseEntity<Results> createResult(@RequestBody Results results) {
        return ResponseEntity.ok(resultService.createResult(results));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Results> updateResult(@PathVariable Long id, @RequestBody Results result) {
        return ResponseEntity.ok(resultService.updateResult(id, result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.deleteResult(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Results> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getResultById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Results>> getPaginatedResults(Pageable pageable) {
        return ResponseEntity.ok(resultService.getPaginatedResults(pageable));
    }
}
