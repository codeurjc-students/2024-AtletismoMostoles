package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Exceptions.ResourceNotFoundException;
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

    public Page<Discipline> getDisciplines(Pageable pageable) {
        return disciplineRepository.findAll(pageable);
    }

    public Discipline getDisciplineById(Long id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline not found with ID: " + id));
    }

    public Discipline createDiscipline(Discipline discipline) {
        return disciplineRepository.save(discipline);
    }

    public Discipline updateDiscipline(Long id, Discipline updatedDiscipline) {
        Discipline existingDiscipline = getDisciplineById(id);
        existingDiscipline.setName(updatedDiscipline.getName());
        existingDiscipline.setDescription(updatedDiscipline.getDescription());
        existingDiscipline.setImageLink(updatedDiscipline.getImageLink());
        return disciplineRepository.save(existingDiscipline);
    }

    public void deleteDiscipline(Long id) {
        Discipline discipline = getDisciplineById(id);
        disciplineRepository.delete(discipline);
    }
}
