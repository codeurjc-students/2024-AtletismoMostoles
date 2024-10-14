package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Coach;
import com.example.TFG_WebApp.Models.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface CoachRepository extends JpaRepository<Coach, String> {

    Coach findCoachByName(final String name);
    Collection<Coach> findByNameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndDiscipline_NameContainingIgnoreCaseAndLicenseContainsIgnoreCase(String firstName, String lastName, String discipline, String license);
}
