package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Services.CoachService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/coaches")
public class CoachRestController {

    @Autowired
    private CoachService coachService;

    @PostMapping
    public ResponseEntity<Coach> createCoach(@RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.createCoach(coach));
    }

    @PutMapping("/{licenseNumber}")
    public ResponseEntity<Coach> updateCoach(@PathVariable String licenseNumber, @RequestBody Coach coach) {
        return ResponseEntity.ok(coachService.updateCoach(licenseNumber, coach));
    }

    @DeleteMapping("/{licenseNumber}")
    public ResponseEntity<Void> deleteCoach(@PathVariable String licenseNumber) {
        coachService.deleteCoach(licenseNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{licenseNumber}")
    public ResponseEntity<Coach> getCoachById(@PathVariable String licenseNumber) {
        return ResponseEntity.ok(coachService.getCoachById(licenseNumber));
    }

    @GetMapping
    public ResponseEntity<Page<Coach>> getPaginatedAndFilteredCoaches(
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String disciplineName,
            Pageable pageable) {
        return ResponseEntity.ok(coachService.getPaginatedAndFilteredCoaches(licenseNumber, firstName, lastName, disciplineName, pageable));
    }
}