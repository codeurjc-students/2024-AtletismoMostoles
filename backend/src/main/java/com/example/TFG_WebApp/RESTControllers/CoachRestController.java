package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Services.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coaches")
@CrossOrigin(origins = "https://localhost:4200")
public class CoachRestController {
    @Autowired
    private CoachService coachService;

    @GetMapping
    public ResponseEntity<Page<Coach>> getAllCoaches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Coach> coaches = coachService.getCoaches(pageable);
        return ResponseEntity.ok(coaches);
    }

    @GetMapping("/{licenseNumber}")
    public ResponseEntity<Coach> getCoach(@PathVariable String licenseNumber) {
        Coach coach = coachService.getCoachById(licenseNumber);
        return ResponseEntity.ok(coach);
    }

    @PostMapping
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        Coach createdCoach = coachService.createCoach(coach);
        return new ResponseEntity<>(createdCoach, HttpStatus.CREATED);
    }

    @PutMapping("/{licenseNumber}")
    public ResponseEntity<Coach> updateCoach(@PathVariable String licenseNumber, @RequestBody Coach coach) {
        Coach updatedCoach = coachService.updateCoach(licenseNumber, coach);
        return ResponseEntity.ok(updatedCoach);
    }

    @DeleteMapping("/{licenseNumber}")
    public ResponseEntity<Void> deleteCoach(@PathVariable String licenseNumber) {
        coachService.deleteCoach(licenseNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Coach>> getFilteredCoaches(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String discipline,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Coach> filteredCoaches = coachService.getFilteredCoaches(firstName, lastName, licenseNumber, discipline, pageable);
        return ResponseEntity.ok(filteredCoaches);
    }

}
