package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, String> {
}
