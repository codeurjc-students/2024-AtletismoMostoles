package com.example.service2.repositories;

import com.example.service2.entities.PdfHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfHistoryRepository extends JpaRepository<PdfHistory, Long> {

    List<PdfHistory> findByAthleteId(String athleteId);
}
