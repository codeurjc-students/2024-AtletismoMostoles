package com.example.TFG_WebApp.Repositories;

import com.example.TFG_WebApp.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Collection<Event> findByDateAfter(LocalDate date);

    Collection<Event> findByOrganizers(Boolean organizer);

    List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);

}
