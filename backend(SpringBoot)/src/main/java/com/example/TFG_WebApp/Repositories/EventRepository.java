package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByIsOrganizedByClubAndDateBetween(Boolean organizedByClub, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Event> findByIsOrganizedByClub(Boolean organizedByClub, Pageable pageable);
    Page<Event> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
