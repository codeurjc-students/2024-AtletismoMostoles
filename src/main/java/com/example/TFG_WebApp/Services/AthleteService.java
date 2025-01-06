package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.DuplicateResourceException;
import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Repositories.AthleteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AthleteService {
    @Autowired
    private AthleteRepository athleteRepository;

    public Page<Athlete> getAthletes(Pageable pageable) {
        return athleteRepository.findAll(pageable);
    }

    public Athlete getAthleteById(String licenseNumber) {
        return athleteRepository.findById(licenseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with license number: " + licenseNumber));
    }

    public Athlete createAthlete(Athlete athlete) {
        if (athleteRepository.existsById(athlete.getLicenseNumber())) {
            throw new DuplicateResourceException("Athlete already exists with license number: " + athlete.getLicenseNumber());
        }
        return athleteRepository.save(athlete);
    }

    public Athlete updateAthlete(String licenseNumber, Athlete updatedAthlete) {
        Athlete existingAthlete = getAthleteById(licenseNumber);
        existingAthlete.setFirstName(updatedAthlete.getFirstName());
        existingAthlete.setLastName(updatedAthlete.getLastName());
        existingAthlete.setBirthDate(updatedAthlete.getBirthDate());
        existingAthlete.setCoach(updatedAthlete.getCoach());
        existingAthlete.setDisciplines(updatedAthlete.getDisciplines());
        return athleteRepository.save(existingAthlete);
    }

    public void deleteAthlete(String licenseNumber) {
        Athlete athlete = getAthleteById(licenseNumber);
        athleteRepository.delete(athlete);
    }
}