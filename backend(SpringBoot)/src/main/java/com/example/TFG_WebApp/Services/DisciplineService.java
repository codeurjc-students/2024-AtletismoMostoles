package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.DisciplineRepository;
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
public class DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    public Discipline createDiscipline(Discipline discipline) {
        return disciplineRepository.save(discipline);
    }

    public Discipline updateDiscipline(Long id, Discipline updatedDiscipline) {
        return disciplineRepository.findById(id).map(existingDiscipline -> {
            existingDiscipline.setName(updatedDiscipline.getName());
            existingDiscipline.setCategory(updatedDiscipline.getCategory());
            existingDiscipline.setCoaches(updatedDiscipline.getCoaches());
            existingDiscipline.setAthletes(updatedDiscipline.getAthletes());
            existingDiscipline.setEquipment(updatedDiscipline.getEquipment());
            existingDiscipline.setEvents(updatedDiscipline.getEvents());
            return disciplineRepository.save(existingDiscipline);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
    }

    public void deleteDiscipline(Long id) {
        try {
            disciplineRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found");
        }
    }

    public Discipline getDisciplineById(Long id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discipline not found"));
    }

    public Page<Discipline> getPaginatedAndFilteredDisciplines(String name, String category, Pageable pageable) {
        return disciplineRepository.findAll((Specification<Discipline>) (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (name != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (category != null) {
                predicates = criteriaBuilder.and(predicates, criteriaBuilder.equal(root.get("category"), category));
            }

            return predicates;
        }, pageable);
    }
}
