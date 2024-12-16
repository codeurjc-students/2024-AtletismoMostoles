package com.example.TFG_WebApp.Services;

import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import com.example.TFG_WebApp.Repositories.AthleteRepository;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AthleteService {

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private CoachService coachService;

    @Autowired
    private DisciplineService disciplineService;

    public Page<Athlete> getAllAthletes(Pageable page) {
        return athleteRepository.findAll(page);
    }

    /*
    public Collection<Athlete> filterRanking(String nombre, String apellido, String disciplina, String licencia, String entrenador, String categoria) {
        Coach coach = new Coach();
        List<Discipline> discipline = new ArrayList<>();
        if(entrenador != null){
            coach = coachService.findCoachByName(entrenador);
        }
        if(disciplina != null){
            discipline.add(disciplineService.getDisciplineById(disciplina));
        }

        return athleteRepository.findAthleteByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndDisciplinesContainingIgnoreCaseAndLicenseContainingIgnoreCaseAndCoachContainsIgnoreCaseAndCategoryContainingIgnoreCase
                (nombre != null ? nombre: "",apellido != null ? apellido: "",discipline,licencia != null ? licencia: "",coach,categoria != null ? categoria: "");
    }*/

    public Collection<Athlete> getAthleteByCoach(Coach coach) {
        return athleteRepository.findByCoach(coach);
    }

    public Athlete addAthlete(Athlete athlete) {
        return athleteRepository.save(athlete);
    }

    public Athlete deleteAthlete(Athlete athlete) {
        if(athlete != null){
            athleteRepository.delete(athlete);
        }
        return athlete;
    }
}
