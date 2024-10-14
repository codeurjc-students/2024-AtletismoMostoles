package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Athlete;
import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AthleteRepository extends JpaRepository<Athlete, String> {

    Collection<Athlete> findByCoach(Coach coach);

    /*Collection<Athlete> findAthleteByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndDisciplinesContainingIgnoreCaseAndLicenseContainingIgnoreCaseAndCoachContainsIgnoreCaseAndCategoryContainingIgnoreCase
            (String firstName, String lastName, List<Discipline> discipline, String license, Coach coach, String category);*/
}
