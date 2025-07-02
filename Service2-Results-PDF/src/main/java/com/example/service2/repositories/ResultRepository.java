package com.example.service2.repositories;

import com.example.service2.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    List<Result> findByAthleteId(Long athleteId);

    List<Result> findByEventId(Long eventId);
}
