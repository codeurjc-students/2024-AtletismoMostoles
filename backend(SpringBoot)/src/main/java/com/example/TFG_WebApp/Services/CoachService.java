package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    public Coach createCoach(Coach coach) {
        return coachRepository.save(coach);
    }

    public Coach updateCoach(String licenseNumber, Coach updatedCoach) {
        return coachRepository.findById(licenseNumber).map(existingCoach -> {
            existingCoach.setFirstName(updatedCoach.getFirstName());
            existingCoach.setLastName(updatedCoach.getLastName());
            existingCoach.setDiscipline(updatedCoach.getDiscipline());
            return coachRepository.save(existingCoach);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found"));
    }

    public void deleteCoach(String licenseNumber) {
        try {
            coachRepository.deleteById(licenseNumber);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found");
        }
    }

    public Coach getCoachById(String licenseNumber) {
        return coachRepository.findById(licenseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found"));
    }

    public Page<Coach> getPaginatedAndFilteredCoaches(String licenseNumber, String firstName, String lastName, String disciplineName, Pageable pageable) {
        return coachRepository.findAll((Specification<Coach>) (root, query, criteriaBuilder) -> {
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
            if (disciplineName != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.join("discipline").get("name"), "%" + disciplineName + "%"));
            }
            return predicates;
        }, pageable);
    }
}
