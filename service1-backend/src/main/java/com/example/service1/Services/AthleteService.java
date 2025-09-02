package com.example.service1.Services;

import com.example.service1.Exceptions.DuplicateResourceException;
import com.example.service1.Exceptions.ResourceNotFoundException;
import com.example.service1.Entities.Athlete;
import com.example.service1.Repositories.AthleteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


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

    public Page<Athlete> getFilteredAthletes(String firstName, String lastName, String discipline,
                                             String licenseNumber, String coach, Pageable pageable) {
        Specification<Athlete> spec = Specification.where(null);

        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("firstName"), "%" + firstName + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("lastName"), "%" + lastName + "%"));
        }
        if (discipline != null && !discipline.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.join("disciplines").get("name"), "%" + discipline + "%"));
        }
        if (licenseNumber != null && !licenseNumber.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("licenseNumber"), licenseNumber));
        }
        if (coach != null && !coach.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.join("coach").get("firstName"), "%" + coach + "%"));
        }

        return athleteRepository.findAll(spec, pageable);
    }

}