package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ResultsRepository extends JpaRepository<Results, Long> {
    Page<Results> findByEventId(Long eventId, Pageable pageable);
    Page<Results> findByDisciplineId(Long disciplineId, Pageable pageable);
    Page<Results> findByEventIdAndDisciplineId(Long eventId, Long disciplineId, Pageable pageable);
}
