package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Services.AthleteService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/athletes")
public class AthleteRestController {

    @Autowired
    private AthleteService athleteService;

    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@RequestBody Athlete athlete) {
        return ResponseEntity.ok(athleteService.saveAthlete(athlete));
    }

    @PutMapping("/{licenseNumber}")
    public ResponseEntity<Athlete> updateAthlete(@PathVariable String licenseNumber, @RequestBody Athlete athlete) {
        return ResponseEntity.ok(athleteService.updateAthlete(licenseNumber, athlete));
    }

    @DeleteMapping("/{licenseNumber}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable String licenseNumber) {
        athleteService.deleteAthlete(licenseNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{licenseNumber}")
    public ResponseEntity<Athlete> getAthleteById(@PathVariable String licenseNumber) {
        return ResponseEntity.ok(athleteService.getAthleteById(licenseNumber));
    }

    @GetMapping
    public ResponseEntity<Page<Athlete>> getPaginatedAndFilteredAthletes(
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String coachName,
            @RequestParam(required = false) String disciplineName,
            Pageable pageable) {
        return ResponseEntity.ok(athleteService.getPaginatedAndFilteredAthletes(licenseNumber, firstName, lastName, coachName, disciplineName, pageable));
    }
}