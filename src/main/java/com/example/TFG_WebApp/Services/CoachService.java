package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.CoachRepository;
import com.example.TFG_WebApp.Repositories.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class CoachService {

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private DisciplineRepository disciplineRepository;

    public Page<Coach> getCoaches(Pageable page) {
        return coachRepository.findAll(page);
    }

    public Collection<Coach> filterCoach(String nombre, String apellido, String disciplina, String licencia) {
        return coachRepository.findByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndDiscipline_NameContainingIgnoreCaseAndLicenseContainsIgnoreCase
                (nombre != null ? nombre: "", apellido != null ? apellido: "", disciplina != null ? disciplina: "", licencia != null ? licencia: "");
    }

    public Coach findCoachByLicense(String license){
        return coachRepository.findById(license).get();
    }

    public Coach findCoachByName(String nombre){
        return coachRepository.findCoachByName(nombre);
    }
}
