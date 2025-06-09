package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import com.example.TFG_WebApp.Repositories.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Autowired
    private CoachRepository coachRepository;

    public Page<Discipline> getDisciplines(Pageable pageable) {
        return disciplineRepository.findAll(pageable);
    }

    public Discipline getDisciplineById(Long id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with ID: " + id));
    }

    public Discipline createDiscipline(Discipline discipline) {
        Set<Coach> coaches = discipline.getCoaches();
        if (coaches != null) {
            discipline.setCoaches(fetchExistingCoaches(coaches));
        }
        return disciplineRepository.save(discipline);
    }

    public Discipline updateDiscipline(Long id, Discipline updatedDiscipline) {
        Discipline existingDiscipline = getDisciplineById(id);
        existingDiscipline.setName(updatedDiscipline.getName());
        existingDiscipline.setDescription(updatedDiscipline.getDescription());
        existingDiscipline.setImageLink(updatedDiscipline.getImageLink());
        existingDiscipline.setEquipment(updatedDiscipline.getEquipment());
        existingDiscipline.setAthletes(updatedDiscipline.getAthletes());
        existingDiscipline.setEvents(updatedDiscipline.getEvents());

        Set<Coach> coaches = updatedDiscipline.getCoaches();
        if (coaches != null) {
            existingDiscipline.setCoaches(fetchExistingCoaches(coaches));
        }
        return disciplineRepository.save(existingDiscipline);
    }

    public void deleteDiscipline(Long id) {
        Discipline discipline = getDisciplineById(id);
        disciplineRepository.delete(discipline);
    }

    private Set<Coach> fetchExistingCoaches(Set<Coach> coaches) {
        return coaches.stream()
                .map(coach -> coachRepository.findById(coach.getLicenseNumber())
                        .orElseThrow(() -> new ResourceNotFoundException("Coach not found with license number: " + coach.getLicenseNumber())))
                .collect(Collectors.toSet());
    }
}
