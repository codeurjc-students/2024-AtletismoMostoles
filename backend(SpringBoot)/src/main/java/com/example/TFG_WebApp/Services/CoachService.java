package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.DuplicateResourceException;
import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CoachService {
    @Autowired
    private CoachRepository coachRepository;

    public Page<Coach> getCoaches(Pageable pageable) {
        return coachRepository.findAll(pageable);
    }

    public Coach getCoachById(String licenseNumber) {
        return coachRepository.findById(licenseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Coach not found with license number: " + licenseNumber));
    }

    public Coach createCoach(Coach coach) {
        if (coachRepository.existsById(coach.getLicenseNumber())) {
            throw new DuplicateResourceException("Coach already exists with license number: " + coach.getLicenseNumber());
        }
        return coachRepository.save(coach);
    }

    public Coach updateCoach(String licenseNumber, Coach updatedCoach) {
        Coach existingCoach = getCoachById(licenseNumber);
        existingCoach.setFirstName(updatedCoach.getFirstName());
        existingCoach.setLastName(updatedCoach.getLastName());
        existingCoach.setDisciplines(updatedCoach.getDisciplines());
        return coachRepository.save(existingCoach);
    }

    public void deleteCoach(String licenseNumber) {
        Coach coach = getCoachById(licenseNumber);
        coachRepository.delete(coach);
    }
}