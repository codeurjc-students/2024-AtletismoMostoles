package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Services.DisciplineService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineRestController {

    @Autowired
    private DisciplineService disciplineService;

    @PostMapping
    public ResponseEntity<Discipline> createDiscipline(@RequestBody Discipline discipline) {
        return ResponseEntity.ok(disciplineService.createDiscipline(discipline));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discipline> updateDiscipline(@PathVariable Long id, @RequestBody Discipline discipline) {
        return ResponseEntity.ok(disciplineService.updateDiscipline(id, discipline));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscipline(@PathVariable Long id) {
        disciplineService.deleteDiscipline(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discipline> getDisciplineById(@PathVariable Long id) {
        return ResponseEntity.ok(disciplineService.getDisciplineById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Discipline>> getPaginatedAndFilteredDisciplines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return ResponseEntity.ok(disciplineService.getPaginatedAndFilteredDisciplines(name, category, pageable));
    }
}
