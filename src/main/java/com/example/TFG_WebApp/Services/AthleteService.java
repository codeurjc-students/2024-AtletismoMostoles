package com.example.TFG_WebApp.Services;

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


@Service
public class AthleteService {

    @Autowired
    private AthleteRepository athleteRepository;

    public Athlete saveAthlete(Athlete athlete) {
        return athleteRepository.save(athlete);
    }

    public Athlete updateAthlete(String licenseNumber, Athlete updatedAthlete) {
        return athleteRepository.findById(licenseNumber).map(existingAthlete -> {
            existingAthlete.setFirstName(updatedAthlete.getFirstName());
            existingAthlete.setLastName(updatedAthlete.getLastName());
            existingAthlete.setBirthDate(updatedAthlete.getBirthDate());
            existingAthlete.setCoach(updatedAthlete.getCoach());
            existingAthlete.setDisciplines(updatedAthlete.getDisciplines());
            return athleteRepository.save(existingAthlete);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Athlete not found"));
    }

    public void deleteAthlete(String licenseNumber) {
        try {
            athleteRepository.deleteById(licenseNumber);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Athlete not found");
        }
    }

    public Athlete getAthleteById(String licenseNumber) {
        return athleteRepository.findById(licenseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Athlete not found"));
    }

    public Page<Athlete> getPaginatedAndFilteredAthletes(String licenseNumber, String firstName, String lastName, String coachName, String disciplineName, Pageable pageable) {
        return athleteRepository.findAll((Specification<Athlete>) (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (licenseNumber != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("licenseNumber"), licenseNumber));
            }
            if (firstName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
            }
            if (lastName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
            }
            if (coachName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.join("coach").get("firstName"), "%" + coachName + "%"));
            }
            if (disciplineName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.join("disciplines").get("name"), "%" + disciplineName + "%"));
            }
            return predicates;
        }, pageable);
    }
}