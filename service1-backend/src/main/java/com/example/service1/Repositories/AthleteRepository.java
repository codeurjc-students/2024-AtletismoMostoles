package com.example.service1.Repositories;

import com.example.service1.Models.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, String>, JpaSpecificationExecutor<Athlete> {

}
