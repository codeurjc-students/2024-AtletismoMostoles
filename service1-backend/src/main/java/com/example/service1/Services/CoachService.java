package com.example.service1.Services;


import com.example.service1.Exceptions.DuplicateResourceException;
import com.example.service1.Exceptions.ResourceNotFoundException;
import com.example.service1.Entities.Coach;
import com.example.service1.Repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<Coach> getFilteredCoaches(String firstName, String lastName, String licenseNumber,
                                          String discipline, Pageable pageable) {
        Specification<Coach> spec = Specification.where(null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
        }
        if (licenseNumber != null && !licenseNumber.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("licenseNumber"), licenseNumber));
        }
        if (discipline != null && !discipline.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.join("disciplines").get("name"), "%" + discipline + "%"));
        }

        return coachRepository.findAll(spec, pageable);
    }

}