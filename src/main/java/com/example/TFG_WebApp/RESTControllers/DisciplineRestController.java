package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Services.DisciplineService;
import com.example.TFG_WebApp.Services.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineRestController {

    @Autowired
    private DisciplineService disciplineService;

    @Autowired
    private CoachService coachService;

    @GetMapping
    public ResponseEntity<Page<Discipline>> getAllDisciplines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Discipline> disciplines = disciplineService.getDisciplines(pageable);
        return ResponseEntity.ok(disciplines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Discipline> getDiscipline(@PathVariable Long id) {
        Discipline discipline = disciplineService.getDisciplineById(id);
        return ResponseEntity.ok(discipline);
    }

    @PostMapping
    public ResponseEntity<Discipline> createDiscipline(@RequestBody Discipline discipline) {
        Set<Coach> coaches = discipline.getCoaches();
        if (coaches != null && !coaches.isEmpty()) {
            coaches.forEach(coach -> {
                Coach existingCoach = coachService.getCoachById(coach.getLicenseNumber());
                discipline.getCoaches().add(existingCoach);
            });
        }
        Discipline createdDiscipline = disciplineService.createDiscipline(discipline);
        return new ResponseEntity<>(createdDiscipline, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discipline> updateDiscipline(@PathVariable Long id, @RequestBody Discipline discipline) {
        Set<Coach> coaches = discipline.getCoaches();
        if (coaches != null && !coaches.isEmpty()) {
            coaches.forEach(coach -> {
                Coach existingCoach = coachService.getCoachById(coach.getLicenseNumber());
                discipline.getCoaches().add(existingCoach);
            });
        }
        Discipline updatedDiscipline = disciplineService.updateDiscipline(id, discipline);
        return ResponseEntity.ok(updatedDiscipline);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscipline(@PathVariable Long id) {
        disciplineService.deleteDiscipline(id);
        return ResponseEntity.noContent().build();
    }
}
