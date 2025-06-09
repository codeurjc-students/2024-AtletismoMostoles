package com.example.TFG_WebApp.RESTControllers;

import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Services.AthleteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@CrossOrigin(origins = "https://localhost:4200")
public class AthleteRestController {
    @Autowired
    private AthleteService athleteService;

    @GetMapping
    public ResponseEntity<Page<Athlete>> getAllAthletes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Athlete> athletes = athleteService.getAthletes(pageable);
        return ResponseEntity.ok(athletes);
    }

    @GetMapping("/{licenseNumber}")
    public ResponseEntity<Athlete> getAthlete(@PathVariable String licenseNumber) {
        Athlete athlete = athleteService.getAthleteById(licenseNumber);
        return ResponseEntity.ok(athlete);
    }

    @PostMapping
    public ResponseEntity<Athlete> createAthlete(@Valid @RequestBody Athlete athlete) {
        Athlete createdAthlete = athleteService.createAthlete(athlete);
        return new ResponseEntity<>(createdAthlete, HttpStatus.CREATED);
    }

    @PutMapping("/{licenseNumber}")
    public ResponseEntity<Athlete> updateAthlete(@PathVariable String licenseNumber, @Valid @RequestBody Athlete athlete) {
        Athlete updatedAthlete = athleteService.updateAthlete(licenseNumber, athlete);
        return ResponseEntity.ok(updatedAthlete);
    }

    @DeleteMapping("/{licenseNumber}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable String licenseNumber) {
        athleteService.deleteAthlete(licenseNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Athlete>> getFilteredAthletes(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String discipline,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) String coach,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Athlete> filteredAthletes = athleteService.getFilteredAthletes(firstName, lastName, discipline, licenseNumber, coach, pageable);
        return ResponseEntity.ok(filteredAthletes);
    }
}
